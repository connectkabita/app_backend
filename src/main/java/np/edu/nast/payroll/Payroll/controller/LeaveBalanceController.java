package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.LeaveBalance;
import np.edu.nast.payroll.Payroll.service.LeaveBalanceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-balance")
@CrossOrigin(origins = "http://localhost:5173")
public class LeaveBalanceController {

    private final LeaveBalanceService leaveBalanceService;

    public LeaveBalanceController(LeaveBalanceService service) {
        this.leaveBalanceService = service;
    }

    @PostMapping
    public LeaveBalance create(@RequestBody LeaveBalance balance) {
        return leaveBalanceService.createLeaveBalance(balance);
    }

    @PutMapping("/{id}")
    public LeaveBalance update(@PathVariable Long id, @RequestBody LeaveBalance balance) {
        return leaveBalanceService.updateLeaveBalance(id, balance);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        leaveBalanceService.deleteLeaveBalance(id);
    }

    @GetMapping("/{id}")
    public LeaveBalance getById(@PathVariable Long id) {
        return leaveBalanceService.getLeaveBalanceById(id);
    }

    @GetMapping
    public List<LeaveBalance> getAll() {
        return leaveBalanceService.getAllLeaveBalances();
    }

    @GetMapping("/employee/{empId}")
    public List<LeaveBalance> getByEmployee(@PathVariable Long empId) {
        return leaveBalanceService.getLeaveBalanceByEmployee(empId);
    }
}