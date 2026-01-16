package np.edu.nast.payroll.Payroll.controller;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.entity.EmployeeLeave;
import np.edu.nast.payroll.Payroll.service.EmployeeLeaveService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/employee-leaves")
// Note: We keep @CrossOrigin, but your SecurityConfig will handle most of the heavy lifting.
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*")
@RequiredArgsConstructor
public class EmployeeLeaveController {

    private final EmployeeLeaveService employeeLeaveService;

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Integer id,
            @RequestParam String status,
            @RequestParam Integer adminId) {
        try {
            // Use the service to update status and get the updated entity
            EmployeeLeave updatedLeave = employeeLeaveService.updateLeaveStatus(id, status, adminId);
            return ResponseEntity.ok(updatedLeave);
        } catch (Exception e) {
            // If service throws an error (e.g., ID not found), return 400 with the error message
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<EmployeeLeave> requestLeave(@RequestBody EmployeeLeave leave) {
        return ResponseEntity.ok(employeeLeaveService.requestLeave(leave));
    }

    @GetMapping
    public ResponseEntity<List<EmployeeLeave>> getAll() {
        return ResponseEntity.ok(employeeLeaveService.getAllLeaves());
    }
}