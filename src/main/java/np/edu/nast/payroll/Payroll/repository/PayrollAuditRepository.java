package np.edu.nast.payroll.Payroll.repository;
import np.edu.nast.payroll.Payroll.entity.PayrollAudit;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PayrollAuditRepository extends JpaRepository<PayrollAudit, Long> {}
