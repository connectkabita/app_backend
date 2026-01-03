package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.EmployeeLeave;
import np.edu.nast.payroll.Payroll.service.EmployeeLeaveService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employee-leaves")
@CrossOrigin(origins = "http://localhost:5173")
public class EmployeeLeaveController {

    private final EmployeeLeaveService employeeLeaveService;

    public EmployeeLeaveController(EmployeeLeaveService service) {
        this.employeeLeaveService = service;
    }

    @PostMapping
    public EmployeeLeave requestLeave(@RequestBody EmployeeLeave leave) {
        return employeeLeaveService.requestLeave(leave);
    }

    @GetMapping
    public List<EmployeeLeave> getAll() {
        return employeeLeaveService.getAllLeaves();
    }

    @PatchMapping("/{id}/status")
    public EmployeeLeave updateStatus(@PathVariable Integer id, @RequestBody Map<String, Object> statusUpdate) {
        String status = (String) statusUpdate.get("status");

        // Extract adminId from JSON and convert to Integer to match Service requirements
        Object adminIdObj = statusUpdate.get("adminId");
        Integer adminId = (adminIdObj != null) ? Integer.parseInt(adminIdObj.toString()) : 1;

        // Passed 3 arguments to satisfy the service interface
        return employeeLeaveService.updateLeaveStatus(id, status, adminId);
    }
}