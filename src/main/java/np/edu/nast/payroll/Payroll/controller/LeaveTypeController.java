package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.LeaveType;
import np.edu.nast.payroll.Payroll.service.LeaveTypeService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/leave-types")
public class LeaveTypeController {

    private final LeaveTypeService leaveTypeService;

    public LeaveTypeController(LeaveTypeService leaveTypeService) {
        this.leaveTypeService = leaveTypeService;
    }

    @PostMapping
    public LeaveType create(@RequestBody LeaveType leaveType) {
        return leaveTypeService.createLeaveType(leaveType);
    }

    @GetMapping
    public List<LeaveType> getAll() {
        return leaveTypeService.getAllLeaveTypes();
    }

    @PutMapping("/{id}")
    // CHANGE: Long to Integer
    public LeaveType update(@PathVariable Integer id, @RequestBody LeaveType leaveType) {
        return leaveTypeService.updateLeaveType(id, leaveType);
    }

    @DeleteMapping("/{id}")
    // CHANGE: Long to Integer
    public void delete(@PathVariable Integer id) {
        leaveTypeService.deleteLeaveType(id);
    }
}