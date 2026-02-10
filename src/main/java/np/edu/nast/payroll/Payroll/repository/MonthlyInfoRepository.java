package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.MonthlyInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MonthlyInfoRepository extends JpaRepository<MonthlyInfo, Integer> {
    // This resolves the error in PayrollServiceImpl:132
    Optional<MonthlyInfo> findByMonthNameAndStatus(String monthName, String status);
}