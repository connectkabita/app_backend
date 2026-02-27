package np.edu.nast.payroll.Payroll.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import np.edu.nast.payroll.Payroll.entity.EmployeeLeave;
import np.edu.nast.payroll.Payroll.entity.User;
import np.edu.nast.payroll.Payroll.repository.UserRepository;
import np.edu.nast.payroll.Payroll.service.EmployeeLeaveService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/leave")
@RequiredArgsConstructor
public class EmployeeLeaveController {

    private final EmployeeLeaveService employeeLeaveService;
    private final UserRepository userRepository;

    /**
     * GET history for the logged-in user.
     * Allowed for both regular users/employees and admins.
     */
    @GetMapping("/my-history")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'EMPLOYEE') or hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    public ResponseEntity<?> getMyLeaveHistory(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                log.warn("Unauthorized access attempt to /my-history");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "User not authenticated"));
            }

            log.info("Request by: {} | Authorities: {}", userDetails.getUsername(), userDetails.getAuthorities());

            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found in database"));

            if (user.getEmployee() == null) {
                log.error("User {} exists but has no linked Employee profile", userDetails.getUsername());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "No employee profile linked to this account"));
            }

            List<EmployeeLeave> history = employeeLeaveService.getLeavesByEmployee(user.getEmployee().getEmpId());
            return ResponseEntity.ok(history);

        } catch (Exception e) {
            log.error("Error in getMyLeaveHistory: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Apply for a new leave.
     */
    @PostMapping("/apply")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    public ResponseEntity<?> applyLeave(@RequestBody EmployeeLeave leave) {
        try {
            log.info("New leave request for Employee ID: {}", leave.getEmployee().getEmpId());
            EmployeeLeave savedLeave = employeeLeaveService.requestLeave(leave);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedLeave);
        } catch (Exception e) {
            log.error("Leave application failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * ADMIN ONLY: Approve or Reject a leave.
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> updateStatus(@PathVariable Integer id, @RequestBody Map<String, Object> payload) {
        try {
            String status = (String) payload.getOrDefault("status", "PENDING");
            String reason = (String) payload.get("rejectionReason");

            Object rawAdminId = payload.get("adminId");
            Integer adminId = (rawAdminId != null) ? Integer.valueOf(rawAdminId.toString()) : 1;

            EmployeeLeave updated = employeeLeaveService.updateLeaveStatus(id, status, adminId, reason);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<EmployeeLeave>> getAll() {
        return ResponseEntity.ok(employeeLeaveService.getAllLeaves());
    }
}