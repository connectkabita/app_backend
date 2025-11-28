package np.edu.nast.payroll.Payroll.repository;
import np.edu.nast.payroll.Payroll.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Integer> {}
