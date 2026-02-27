package np.edu.nast.payroll.Payroll.dto.auth;

import lombok.Data;

@Data
public class SignupRequestDTO {
    private String username;
    private String email;
    private String password;
    private Integer role_id;
    private String first_name;
    private String last_name;
    private String address;
    private Double basic_salary;
    private String contact;
    private String education;
    private String employment_status;
    private String joining_date;
    private Boolean is_active;
    private String marital_status;
    private Integer position_id;
    private Integer department_id;
}