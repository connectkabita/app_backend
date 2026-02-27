package np.edu.nast.payroll.Payroll.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import np.edu.nast.payroll.Payroll.config.EsewaConfig;
import np.edu.nast.payroll.Payroll.entity.BankAccount;
import np.edu.nast.payroll.Payroll.entity.PayoutInfo;
import np.edu.nast.payroll.Payroll.entity.Payroll;
import np.edu.nast.payroll.Payroll.repository.PayoutInfoRepository;
import np.edu.nast.payroll.Payroll.service.EmailService;
import np.edu.nast.payroll.Payroll.service.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/api/esewa")
@CrossOrigin(origins = "http://localhost:5173")
public class EsewaController {

    @Autowired
    private PayrollService payrollService;

    @Autowired
    private PayoutInfoRepository payoutInfoRepository;

    @Autowired
    private EmailService emailService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Initiates the payment by generating the signature required by eSewa.
     */
    @GetMapping("/initiate/{id}")
    @ResponseBody
    public ResponseEntity<?> initiatePayment(@PathVariable String id) {
        try {
            if (id == null || id.equalsIgnoreCase("undefined") || id.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Invalid Payroll ID: Received 'undefined' or empty."));
            }

            Integer payrollId = Integer.parseInt(id.trim());
            Payroll payroll = payrollService.getPayrollById(payrollId);

            if (payroll == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Payroll record not found."));
            }

            if ("PAID".equalsIgnoreCase(payroll.getStatus())) {
                return ResponseEntity.badRequest().body(Map.of("message", "This payroll is already PAID."));
            }

            // eSewa requires 2 decimal places
            String totalAmount = String.format("%.2f", payroll.getNetSalary());
            // Unique UUID: includes payroll ID for extraction in success callback
            String transactionUuid = "NAST-PAY-" + payrollId + "-" + System.currentTimeMillis();
            String productCode = EsewaConfig.PRODUCT_CODE;

            String signature = generateSignature(totalAmount, transactionUuid, productCode);

            Map<String, String> responseData = Map.of(
                    "amount", totalAmount,
                    "tax_amount", "0",
                    "total_amount", totalAmount,
                    "transaction_uuid", transactionUuid,
                    "product_code", productCode,
                    "signature", signature,
                    "esewa_url", EsewaConfig.ESEWA_GATEWAY_URL,
                    "success_url", EsewaConfig.SUCCESS_URL,
                    "failure_url", EsewaConfig.FAILURE_URL
            );

            log.info("Initiating eSewa payment for Payroll ID: {} with UUID: {}", payrollId, transactionUuid);
            return ResponseEntity.ok(responseData);

        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Invalid ID format. Received: " + id));
        } catch (Exception e) {
            log.error("Payment Initiation Failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Payment Initiation Failed: " + e.getMessage()));
        }
    }

    /**
     * Callback for successful eSewa payment.
     */
    @GetMapping("/success")
    @Transactional
    public String handleSuccess(@RequestParam("data") String base64Data) {
        try {
            // 1. Decode and Parse eSewa Response
            String decodedString = new String(Base64.getDecoder().decode(base64Data));
            JsonNode response = objectMapper.readTree(decodedString);

            String status = response.get("status").asText();
            String transactionUuid = response.get("transaction_uuid").asText();
            String esewaRefId = response.has("transaction_code") ? response.get("transaction_code").asText() : "N/A";

            // 2. Extract Payroll ID from the UUID (NAST-PAY-{ID}-TIMESTAMP)
            Integer payrollId = Integer.parseInt(transactionUuid.split("-")[2]);

            if ("COMPLETE".equalsIgnoreCase(status)) {
                log.info("eSewa payment successful for Payroll ID: {}. Ref: {}", payrollId, esewaRefId);

                // 3. Finalize Payroll Status
                payrollService.finalizePayroll(payrollId, esewaRefId);

                // 4. Create Payout Record
                Payroll payroll = payrollService.getPayrollById(payrollId);
                BankAccount primaryAccount = payroll.getEmployee().getPrimaryBankAccount();

                PayoutInfo payout = PayoutInfo.builder()
                        .payroll(payroll)
                        .employee(payroll.getEmployee())
                        .monthlyInfo(payroll.getMonthlyInfo())
                        .paymentDate(LocalDate.now())
                        .paymentMethod(payroll.getPaymentMethod())
                        .bankAccount(primaryAccount)
                        .amount(payroll.getNetSalary())
                        .paymentStatus("SUCCESS")
                        .transactionReference(esewaRefId)
                        .createdAt(LocalDateTime.now())
                        .build();

                payoutInfoRepository.save(payout);

                // 5. Send Payslip Email
                try {
                    emailService.generateAndSendPayslip(payroll, esewaRefId);
                } catch (Exception e) {
                    log.error("Email delivery failed for Payroll ID: {}", payrollId, e);
                }

                return "redirect:http://localhost:5173/admin/payroll?status=success";
            }
            return "redirect:http://localhost:5173/admin/payroll?status=incomplete";
        } catch (Exception e) {
            log.error("Error processing eSewa success callback", e);
            return "redirect:http://localhost:5173/admin/payroll?status=error";
        }
    }

    /**
     * Callback for failed or cancelled eSewa payment.
     */
    @GetMapping("/failure")
    @Transactional
    public String handleFailure(@RequestParam("data") String base64Data) {
        try {
            String decodedString = new String(Base64.getDecoder().decode(base64Data));
            JsonNode response = objectMapper.readTree(decodedString);
            String transactionUuid = response.get("transaction_uuid").asText();

            // Extract Payroll ID
            Integer payrollId = Integer.parseInt(transactionUuid.split("-")[2]);

            log.warn("eSewa payment failed for Payroll ID: {}. Rolling back record.", payrollId);

            // Rollback removes the 'PROCESSING' record so user can try again
            payrollService.rollbackPayroll(payrollId);

        } catch (Exception e) {
            log.error("Error processing eSewa failure callback", e);
        }
        return "redirect:http://localhost:5173/admin/payroll?status=failed";
    }

    /**
     * Generates HMAC-SHA256 signature for eSewa security.
     */
    private String generateSignature(String totalAmount, String uuid, String productCode) {
        try {
            String message = "total_amount=" + totalAmount + ",transaction_uuid=" + uuid + ",product_code=" + productCode;
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(EsewaConfig.SECRET_KEY.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] hash = sha256_HMAC.doFinal(message.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Signature generation failed: " + e.getMessage());
        }
    }
}