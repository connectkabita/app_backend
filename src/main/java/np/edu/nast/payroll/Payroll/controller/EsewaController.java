package np.edu.nast.payroll.Payroll.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import np.edu.nast.payroll.Payroll.config.EsewaConfig;
import np.edu.nast.payroll.Payroll.entity.BankAccount;
import np.edu.nast.payroll.Payroll.entity.PayoutInfo;
import np.edu.nast.payroll.Payroll.entity.Payroll;
import np.edu.nast.payroll.Payroll.repository.PayoutInfoRepository;
import np.edu.nast.payroll.Payroll.service.EmailService;
import np.edu.nast.payroll.Payroll.service.impl.PayrollServiceImpl;
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

@Controller
@RequestMapping("/api/esewa")
@CrossOrigin(origins = "http://localhost:5173")
public class EsewaController {

    @Autowired
    private PayrollServiceImpl payrollService;

    @Autowired
    private PayoutInfoRepository payoutInfoRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Updated to handle String ID to catch "undefined" from frontend
     * and prevent TypeMismatchErrors.
     */
    @GetMapping("/initiate/{id}")
    @ResponseBody
    public ResponseEntity<?> initiatePayment(@PathVariable String id) {
        try {
            // 1. Check for 'undefined' or null before parsing
            if (id == null || id.equalsIgnoreCase("undefined")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Invalid Payroll ID: Received 'undefined'. Ensure Stage 1 (Process) returned a valid ID."));
            }

            Integer payrollId = Integer.parseInt(id);
            Payroll payroll = payrollService.getPayrollById(payrollId);

            if ("PAID".equalsIgnoreCase(payroll.getStatus())) {
                return ResponseEntity.badRequest().body(Map.of("message", "This payroll is already PAID."));
            }

            // 2. Prepare eSewa Parameters
            String totalAmount = String.format("%.2f", payroll.getNetSalary());

            // UUID format: NAST-PAY-{ID}-{TIMESTAMP}
            String transactionUuid = "NAST-PAY-" + payrollId + "-" + System.currentTimeMillis();
            String productCode = EsewaConfig.PRODUCT_CODE;

            // 3. Generate Signature
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

            return ResponseEntity.ok(responseData);

        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Invalid ID format: Expected a number but received '" + id + "'"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Payment Initiation Failed: " + e.getMessage()));
        }
    }

    @GetMapping("/success")
    @Transactional
    public String handleSuccess(@RequestParam("data") String base64Data) {
        try {
            // Decode eSewa Response
            String decodedString = new String(Base64.getDecoder().decode(base64Data));
            JsonNode response = new ObjectMapper().readTree(decodedString);

            String status = response.get("status").asText();
            String transactionUuid = response.get("transaction_uuid").asText();
            String esewaRefId = response.has("transaction_code") ? response.get("transaction_code").asText() : "N/A";

            // Extract payroll ID from the UUID string (NAST-PAY-{ID}-{TIMESTAMP})
            Integer payrollId = Integer.parseInt(transactionUuid.split("-")[2]);

            if ("COMPLETE".equalsIgnoreCase(status)) {
                // 1. Mark as PAID in database
                payrollService.finalizePayroll(payrollId, esewaRefId);

                Payroll payroll = payrollService.getPayrollById(payrollId);
                BankAccount primaryAccount = payroll.getEmployee().getPrimaryBankAccount();

                // 2. Create Payout Log
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

                // 3. Send Email Notification
                try {
                    emailService.generateAndSendPayslip(payroll, esewaRefId);
                } catch (Exception e) {
                    System.err.println("Email failed to send, but payment was successful: " + e.getMessage());
                }

                return "redirect:http://localhost:5173/admin/payroll?status=success";
            } else {
                // Payment was not completed successfully
                return "redirect:http://localhost:5173/admin/payroll?status=incomplete";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:http://localhost:5173/admin/payroll?status=error&msg=" + e.getMessage();
        }
    }

    @GetMapping("/failure")
    @Transactional
    public String handleFailure(@RequestParam("data") String base64Data) {
        try {
            String decodedString = new String(Base64.getDecoder().decode(base64Data));
            JsonNode response = new ObjectMapper().readTree(decodedString);
            String transactionUuid = response.get("transaction_uuid").asText();

            // Extract ID and remove the pending record to keep DB clean
            Integer payrollId = Integer.parseInt(transactionUuid.split("-")[2]);
            payrollService.rollbackPayroll(payrollId);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:http://localhost:5173/admin/payroll?status=failed";
    }

    private String generateSignature(String totalAmount, String uuid, String productCode) {
        try {
            // eSewa signature string format is strict: total_amount, transaction_uuid, and product_code
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