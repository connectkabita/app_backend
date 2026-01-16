package np.edu.nast.payroll.Payroll.service.impl;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.entity.LeaveBalance;
import np.edu.nast.payroll.Payroll.repository.LeaveBalanceRepository;
import np.edu.nast.payroll.Payroll.service.LeaveBalanceService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveBalanceServiceImpl implements LeaveBalanceService {

    private final LeaveBalanceRepository leaveBalanceRepository;

    @Override
    public List<LeaveBalance> getLeaveBalanceByEmployee(Integer empId) {
        // FIX: Now correctly passes Integer
        return leaveBalanceRepository.findByEmployeeEmpId(empId);
    }

    @Override public LeaveBalance createLeaveBalance(LeaveBalance b) { return leaveBalanceRepository.save(b); }
    @Override public List<LeaveBalance> getAllLeaveBalances() { return leaveBalanceRepository.findAll(); }
    @Override public void deleteLeaveBalance(Long id) { leaveBalanceRepository.deleteById(id); }
    @Override public LeaveBalance getLeaveBalanceById(Long id) { return leaveBalanceRepository.findById(id).orElseThrow(); }
    @Override public LeaveBalance updateLeaveBalance(Long id, LeaveBalance b) {
        LeaveBalance ex = leaveBalanceRepository.findById(id).orElseThrow();
        ex.setCurrentBalanceDays(b.getCurrentBalanceDays());
        return leaveBalanceRepository.save(ex);
    }
}