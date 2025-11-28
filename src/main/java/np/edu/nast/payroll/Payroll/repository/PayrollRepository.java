package np.edu.nast.payroll.Payroll.repository;
import np.edu.nast.payroll.Payroll.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PayrollRepository extends JpaRepository<Payroll, Integer> {}
