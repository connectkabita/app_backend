package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {
    // FIX: Change to Integer to resolve build failure
    List<LeaveBalance> findByEmployeeEmpId(Integer empId);

    // Used for automatic deduction math
    Optional<LeaveBalance> findByEmployeeEmpIdAndLeaveTypeLeaveTypeId(Integer empId, Integer leaveTypeId);
}