package np.edu.nast.payroll.Payroll.service;

import org.springframework.stereotype.Service;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class EsewaService {

    public String generateSignature(String totalAmount, String transactionUuid, String productCode) {
        String secretKey = "8gBm/:&EnhH.1/q"; // Use your actual Secret Key from EsewaConfig
        String data = "total_amount=" + totalAmount + ",transaction_uuid=" + transactionUuid + ",product_code=" + productCode;

        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] hash = sha256_HMAC.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate eSewa signature", e);
        }
    }
}