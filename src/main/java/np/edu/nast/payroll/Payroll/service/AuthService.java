package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.dto.auth.LoginRequestDTO;
import np.edu.nast.payroll.Payroll.dto.auth.LoginResponseDTO;

public interface AuthService {
    // Standardize to this name
    LoginResponseDTO authenticateUser(LoginRequestDTO request);
}