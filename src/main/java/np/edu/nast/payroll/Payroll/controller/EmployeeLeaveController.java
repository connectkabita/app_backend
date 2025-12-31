package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.EmployeeLeave;
import np.edu.nast.payroll.Payroll.service.EmployeeLeaveService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employee-leaves")
@CrossOrigin(origins = "http://localhost:5173")
public class EmployeeLeaveController {

    private final EmployeeLeaveService leaveService;

    public EmployeeLeaveController(EmployeeLeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @GetMapping
    public List<EmployeeLeave> getAllLeaves() {
        return leaveService.getAllLeaves(); // Fixed: Matches ServiceImpl
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<EmployeeLeave> updateStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> payload) {

        String status = (String) payload.get("status");
        Integer adminId = (Integer) payload.get("adminId");

        EmployeeLeave updatedLeave = leaveService.updateLeaveStatus(id, status, adminId);
        return ResponseEntity.ok(updatedLeave);
    }

    @GetMapping("/employee/{empId}")
    public List<EmployeeLeave> getByEmployee(@PathVariable Integer empId) {
        return leaveService.getLeavesByEmployee(empId); // Fixed: Matches ServiceImpl
    }
}