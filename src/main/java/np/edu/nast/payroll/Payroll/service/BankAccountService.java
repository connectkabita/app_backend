package np.edu.nast.payroll.Payroll.service;
import np.edu.nast.payroll.Payroll.entity.BankAccount;
import java.util.List;
public interface BankAccountService {
    BankAccount create(BankAccount account);
    BankAccount update(Integer id, BankAccount account);
    void delete(Integer id);
    BankAccount getById(Integer id);
    List<BankAccount> getAll();
    List<BankAccount> findByEmployeeId(Integer empId);
}
