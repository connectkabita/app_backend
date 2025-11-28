package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.LeaveBalance;
import np.edu.nast.payroll.Payroll.repository.LeaveBalanceRepository;
import np.edu.nast.payroll.Payroll.service.LeaveBalanceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaveBalanceServiceImpl implements LeaveBalanceService {

    private final LeaveBalanceRepository leaveBalanceRepository;

    public LeaveBalanceServiceImpl(LeaveBalanceRepository repo) {
        this.leaveBalanceRepository = repo;
    }

    @Override
    public LeaveBalance createLeaveBalance(LeaveBalance balance) {
        return leaveBalanceRepository.save(balance);
    }

    @Override
    public LeaveBalance updateLeaveBalance(Long id, LeaveBalance balance) {
        LeaveBalance existing = leaveBalanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave balance not found"));

        existing.setCurrentBalanceDays(balance.getCurrentBalanceDays());
        existing.setYear(balance.getYear());

        return leaveBalanceRepository.save(existing);
    }

    @Override
    public void deleteLeaveBalance(Long id) {
        leaveBalanceRepository.deleteById(id);
    }

    @Override
    public LeaveBalance getLeaveBalanceById(Long id) {
        return leaveBalanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave balance not found"));
    }

    @Override
    public List<LeaveBalance> getAllLeaveBalances() {
        return leaveBalanceRepository.findAll();
    }

    @Override
    public List<LeaveBalance> getLeaveBalanceByEmployee(Long empId) {
        return leaveBalanceRepository.findByEmployeeEmpId(empId);
    }
}
