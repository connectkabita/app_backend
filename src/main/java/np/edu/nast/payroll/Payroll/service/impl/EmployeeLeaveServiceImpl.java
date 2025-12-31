package np.edu.nast.payroll.Payroll.service.impl;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.entity.Employee;
import np.edu.nast.payroll.Payroll.entity.EmployeeLeave;
import np.edu.nast.payroll.Payroll.entity.LeaveType;
import np.edu.nast.payroll.Payroll.entity.User;
import np.edu.nast.payroll.Payroll.repository.EmployeeLeaveRepository;
import np.edu.nast.payroll.Payroll.repository.EmployeeRepository;
import np.edu.nast.payroll.Payroll.repository.LeaveTypeRepository;
import np.edu.nast.payroll.Payroll.repository.UserRepository;
import np.edu.nast.payroll.Payroll.service.EmployeeLeaveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeLeaveServiceImpl implements EmployeeLeaveService {

    private final EmployeeLeaveRepository employeeLeaveRepo;
    private final UserRepository userRepo;
    private final EmployeeRepository employeeRepo;
    private final LeaveTypeRepository leaveTypeRepo;

    @Override
    public List<EmployeeLeave> getAllLeaves() {
        return employeeLeaveRepo.findAll();
    }

    @Override
    @Transactional
    public EmployeeLeave updateLeaveStatus(Integer id, String status, Integer adminId) {
        EmployeeLeave leave = employeeLeaveRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave record not found with ID: " + id));

        leave.setStatus(status);

        if ("Approved".equalsIgnoreCase(status)) {
            User admin = userRepo.findById(adminId)
                    .orElseThrow(() -> new IllegalArgumentException("Admin User not found with ID: " + adminId));

            leave.setApprovedBy(admin);
            leave.setApprovedAt(LocalDateTime.now());
        } else {
            leave.setApprovedBy(null);
            leave.setApprovedAt(null);
        }

        return employeeLeaveRepo.save(leave);
    }

    @Override
    @Transactional
    public EmployeeLeave requestLeave(EmployeeLeave leave) {
        // 1. Validate and Fetch Employee
        if (leave.getEmployee() == null || leave.getEmployee().getEmpId() == null) {
            throw new IllegalArgumentException("Employee ID is required");
        }
        Employee employee = employeeRepo.findById(leave.getEmployee().getEmpId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        leave.setEmployee(employee);

        // 2. Validate and Fetch LeaveType
        if (leave.getLeaveType() == null || leave.getLeaveType().getLeaveTypeId() == null) {
            throw new IllegalArgumentException("Leave Type ID is required");
        }
        LeaveType leaveType = leaveTypeRepo.findById(leave.getLeaveType().getLeaveTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Leave Type not found"));
        leave.setLeaveType(leaveType);

        // 3. Calculate Total Days
        calculateAndSetTotalDays(leave);

        // 4. Set Default Status
        if (leave.getStatus() == null || leave.getStatus().isEmpty()) {
            leave.setStatus("Pending");
        }

        return employeeLeaveRepo.save(leave);
    }

    @Override
    public EmployeeLeave getLeaveById(Integer id) {
        return employeeLeaveRepo.findById(id).orElse(null);
    }

    @Override
    public List<EmployeeLeave> getLeavesByEmployee(Integer empId) {
        Employee employee = employeeRepo.findById(empId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + empId));
        return employeeLeaveRepo.findAllByEmployee(employee);
    }

    @Override
    public void deleteLeave(Integer id) {
        EmployeeLeave leave = employeeLeaveRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Leave not found with ID: " + id));
        employeeLeaveRepo.delete(leave);
    }

    @Override
    @Transactional
    public EmployeeLeave updateLeave(Integer id, EmployeeLeave leave) {
        EmployeeLeave existingLeave = employeeLeaveRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Leave not found with ID: " + id));

        // Update basic info
        existingLeave.setStartDate(leave.getStartDate());
        existingLeave.setEndDate(leave.getEndDate());
        existingLeave.setReason(leave.getReason());
        existingLeave.setStatus(leave.getStatus());

        // Re-calculate days
        calculateAndSetTotalDays(existingLeave);

        // Validate and update LeaveType if changed
        if (leave.getLeaveType() != null && leave.getLeaveType().getLeaveTypeId() != null) {
            LeaveType leaveType = leaveTypeRepo.findById(leave.getLeaveType().getLeaveTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("Leave Type not found"));
            existingLeave.setLeaveType(leaveType);
        }

        return employeeLeaveRepo.save(existingLeave);
    }

    /**
     * Helper method to calculate duration between start and end date
     */
    private void calculateAndSetTotalDays(EmployeeLeave leave) {
        if (leave.getStartDate() != null && leave.getEndDate() != null) {
            long days = ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate()) + 1;
            if (days <= 0) {
                throw new IllegalArgumentException("End date must be after start date");
            }
            leave.setTotalDays((int) days);
        }
    }
}