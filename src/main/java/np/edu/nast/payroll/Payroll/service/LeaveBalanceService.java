package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.LeaveBalance;
import java.util.List;

public interface LeaveBalanceService {
    LeaveBalance createLeaveBalance(LeaveBalance balance);
    LeaveBalance updateLeaveBalance(Long id, LeaveBalance balance);
    void deleteLeaveBalance(Long id);
    LeaveBalance getLeaveBalanceById(Long id);
    List<LeaveBalance> getAllLeaveBalances();
    // FIX: Change Long to Integer to align with Repository
    List<LeaveBalance> getLeaveBalanceByEmployee(Integer empId);
}