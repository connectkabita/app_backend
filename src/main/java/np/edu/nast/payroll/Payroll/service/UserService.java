package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.User;
import java.util.List;

public interface UserService {
    User create(User user);
    User update(Integer id, User user);
    void delete(Integer id);
    User getById(Integer id);
    List<User> getAll();
}
