package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.dto.auth.LoginRequestDTO;
import np.edu.nast.payroll.Payroll.dto.auth.LoginResponseDTO;
import np.edu.nast.payroll.Payroll.dto.auth.SignupRequestDTO;

public interface AuthService {
    LoginResponseDTO authenticateUser(LoginRequestDTO request);

    void registerUser(SignupRequestDTO request);

    // Add this to handle the final step of the password reset
    void updatePassword(String email, String newPassword);
}