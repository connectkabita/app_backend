package np.edu.nast.payroll.Payroll.service.impl;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.entity.EmployeeLeave;
import np.edu.nast.payroll.Payroll.entity.User;
import np.edu.nast.payroll.Payroll.repository.EmployeeLeaveRepository;
import np.edu.nast.payroll.Payroll.repository.UserRepository;
import np.edu.nast.payroll.Payroll.service.EmployeeLeaveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeLeaveServiceImpl implements EmployeeLeaveService {

    private final EmployeeLeaveRepository employeeLeaveRepo;
    private final UserRepository userRepo;

    @Override
    public List<EmployeeLeave> getAllLeaves() {
        return employeeLeaveRepo.findAll();
    }

    @Override
    @Transactional
    public EmployeeLeave updateLeaveStatus(Integer id, String status, Integer adminId) {
        EmployeeLeave leave = employeeLeaveRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave record not found"));

        leave.setStatus(status);

        if ("Approved".equalsIgnoreCase(status)) {
            // Find the admin user who is performing the approval
            User admin = userRepo.findById(adminId)
                    .orElseThrow(() -> new RuntimeException("Admin User not found with ID: " + adminId));

            leave.setApprovedBy(admin); // Updates the approved_by column
            leave.setApprovedAt(LocalDateTime.now()); // Updates the approved_at column
        } else {
            // If rejected, clear approval audit data
            leave.setApprovedBy(null);
            leave.setApprovedAt(null);
        }

        return employeeLeaveRepo.save(leave);
    }

    @Override public EmployeeLeave requestLeave(EmployeeLeave leave) { return employeeLeaveRepo.save(leave); }
    @Override public EmployeeLeave getLeaveById(Integer id) { return employeeLeaveRepo.findById(id).orElse(null); }
    @Override public List<EmployeeLeave> getLeavesByEmployee(Integer empId) { return employeeLeaveRepo.findAll(); }
    @Override public void deleteLeave(Integer id) { employeeLeaveRepo.deleteById(id); }
    @Override public EmployeeLeave updateLeave(Integer id, EmployeeLeave leave) { return employeeLeaveRepo.save(leave); }
}