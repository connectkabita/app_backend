package np.edu.nast.payroll.Payroll.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayrollRequest {

    @JsonProperty("empId")
    private Integer empId;

    @JsonProperty("grossSalary")
    private Double grossSalary;

    @JsonProperty("totalAllowances")
    private Double totalAllowances;

    @JsonProperty("totalDeductions")
    private Double totalDeductions;

    // These fields allow the frontend to specify IDs, or the backend will use defaults
    @JsonProperty("accountId")
    private Integer accountId;

    @JsonProperty("paymentMethodId")
    private Integer paymentMethodId;

    @JsonProperty("payGroupId")
    private Integer payGroupId;
}