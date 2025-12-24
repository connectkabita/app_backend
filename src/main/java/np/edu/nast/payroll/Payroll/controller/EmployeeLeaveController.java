package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.EmployeeLeave;
import np.edu.nast.payroll.Payroll.service.EmployeeLeaveService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee-leaves")
public class EmployeeLeaveController {

    private final EmployeeLeaveService employeeLeaveService;

    public EmployeeLeaveController(EmployeeLeaveService service) {
        this.employeeLeaveService = service;
    }

    @PostMapping
    public EmployeeLeave requestLeave(@RequestBody EmployeeLeave leave) {
        return employeeLeaveService.requestLeave(leave);
    }

    @PutMapping("/{id}")
    public EmployeeLeave updateLeave(@PathVariable Integer id, @RequestBody EmployeeLeave leave) {
        return employeeLeaveService.updateLeave(id, leave);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        employeeLeaveService.deleteLeave(id);
    }

    @GetMapping("/{id}")
    public EmployeeLeave getById(@PathVariable Integer id) {
        return employeeLeaveService.getLeaveById(id);
    }

    @GetMapping
    public List<EmployeeLeave> getAll() {
        return employeeLeaveService.getAllLeaves();
    }

    @GetMapping("/employee/{empId}")
    public List<EmployeeLeave> getByEmployee(@PathVariable Integer empId) {
        return employeeLeaveService.getLeavesByEmployee(empId);
    }
}
