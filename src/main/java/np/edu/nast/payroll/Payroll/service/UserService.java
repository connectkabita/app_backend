package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.util.List;

public interface UserService extends UserDetailsService {
    User create(User user);
    List<User> getAll();
    void delete(Integer id);

    // Auth & Password Reset Methods
    void initiatePasswordReset(String email);
    void resetPassword(String token, String newPassword);
    User getByEmail(String email);

    // Administration Methods
    void sendOtpToAllUsers();
    User setupDefaultAccount(Integer empId);
}