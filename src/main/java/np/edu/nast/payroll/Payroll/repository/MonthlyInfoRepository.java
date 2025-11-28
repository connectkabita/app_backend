package np.edu.nast.payroll.Payroll.repository;
import np.edu.nast.payroll.Payroll.entity.MonthlyInfo;
import org.springframework.data.jpa.repository.JpaRepository;
public interface MonthlyInfoRepository extends JpaRepository<MonthlyInfo, Long> {}
