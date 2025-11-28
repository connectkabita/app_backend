package np.edu.nast.payroll.Payroll.repository;
import np.edu.nast.payroll.Payroll.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface BankAccountRepository extends JpaRepository<BankAccount, Integer> {
    List<BankAccount> findByEmployeeEmpId(Integer empId);
}
