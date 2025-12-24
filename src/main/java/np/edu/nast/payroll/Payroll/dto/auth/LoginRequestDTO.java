package np.edu.nast.payroll.Payroll.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {
    private String usernameOrEmail;
    private String password;

    // 🔹 Add this field for role-based login
    private String role;
}
