package np.edu.nast.payroll.Payroll.repository;
import np.edu.nast.payroll.Payroll.entity.PayoutInfo;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PayoutInfoRepository extends JpaRepository<PayoutInfo, Long> {}
