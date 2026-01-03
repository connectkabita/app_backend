package np.edu.nast.payroll.Payroll.repository;
import np.edu.nast.payroll.Payroll.entity.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance,Long> {
    List<LeaveBalance> findByEmployeeEmpId(Long empId);

    @Query("""
        SELECT COUNT(DISTINCT lb.employee.id)
        FROM LeaveBalance lb
        WHERE lb.currentBalanceDays > 0
    """)
    long countByCurrentBalanceDaysGreaterThan(double days);

}
