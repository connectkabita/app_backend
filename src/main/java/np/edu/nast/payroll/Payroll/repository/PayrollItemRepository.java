package np.edu.nast.payroll.Payroll.repository;
import lombok.extern.java.Log;
import np.edu.nast.payroll.Payroll.entity.PayrollItem;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PayrollItemRepository extends JpaRepository<PayrollItem, Integer> {}
