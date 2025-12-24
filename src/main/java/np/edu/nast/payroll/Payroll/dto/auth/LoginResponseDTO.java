package np.edu.nast.payroll.Payroll.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDTO {
    private Integer userId;
    private String username;
    private String email;
    private String role;
}
