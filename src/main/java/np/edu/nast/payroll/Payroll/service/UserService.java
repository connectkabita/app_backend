package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.User;
import java.util.List;

public interface UserService {
    User setupDefaultAccount(Integer empId);
    User getByEmail(String email);
    void resetPassword(String token, String newPassword);

    // Add this to fix the UserController error
    void sendOtpToAllUsers();

    // Standard CRUD
    List<User> getAll();
    User getById(Integer id);
    User create(User user);
    User update(Integer id, User user);
    void delete(Integer id);
}