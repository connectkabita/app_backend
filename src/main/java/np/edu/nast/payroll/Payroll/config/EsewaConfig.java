package np.edu.nast.payroll.Payroll.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class EsewaConfig {
    public static final String SECRET_KEY = "8gBm/:&EnhH.1/q";
    public static final String PRODUCT_CODE = "EPAYTEST";
    public static final String ESEWA_GATEWAY_URL = "https://rc-epay.esewa.com.np/api/epay/main/v2/form";

    // URLs updated to match the EsewaController RequestMappings
    public static final String SUCCESS_URL = "http://localhost:8080/api/esewa/success";
    public static final String FAILURE_URL = "http://localhost:8080/api/esewa/failure";
}