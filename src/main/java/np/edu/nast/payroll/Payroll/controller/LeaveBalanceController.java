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

    @GetMapping("/employee/{empId}")
    public List<LeaveBalance> getByEmployee(@PathVariable Integer empId) {
        // FIX: PathVariable is now Integer
        return leaveBalanceService.getLeaveBalanceByEmployee(empId);
    }

    @PostMapping public LeaveBalance create(@RequestBody LeaveBalance b) { return leaveBalanceService.createLeaveBalance(b); }
    @GetMapping public List<LeaveBalance> getAll() { return leaveBalanceService.getAllLeaveBalances(); }
}