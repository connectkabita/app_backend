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

    @PutMapping("/{id}")
    public LeaveType update(@PathVariable Long id, @RequestBody LeaveType leaveType) {
        return leaveTypeService.updateLeaveType(id, leaveType);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        leaveTypeService.deleteLeaveType(id);
    }

    @GetMapping("/{id}")
    public LeaveType getById(@PathVariable Long id) {
        return leaveTypeService.getLeaveTypeById(id);
    }

    @GetMapping
    public List<LeaveType> getAll() {
        return leaveTypeService.getAllLeaveTypes();
    }
}
