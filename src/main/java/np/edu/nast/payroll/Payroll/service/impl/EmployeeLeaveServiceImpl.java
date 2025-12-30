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
        // Validate and Fetch Employee
        if (leave.getEmployee() == null || leave.getEmployee().getEmpId() == null) {
            throw new IllegalArgumentException("Employee ID is required");
        }
        Employee employee = employeeRepo.findById(leave.getEmployee().getEmpId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + leave.getEmployee().getEmpId()));
        leave.setEmployee(employee);

        // Validate and Fetch LeaveType
        if (leave.getLeaveType() == null || leave.getLeaveType().getLeaveTypeId() == null) {
            throw new IllegalArgumentException("Leave Type ID is required");
        }
        LeaveType leaveType = leaveTypeRepo.findById(leave.getLeaveType().getLeaveTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Leave Type not found with ID: " + leave.getLeaveType().getLeaveTypeId()));
        leave.setLeaveType(leaveType);

        // --- CALCULATION LOGIC ---
        // Ensure totalDays is calculated before saving
        if (leave.getStartDate() != null && leave.getEndDate() != null) {
            long days = ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate()) + 1;
            leave.setTotalDays((int) days);
        }

        // Set default status if empty
        if (leave.getStatus() == null || leave.getStatus().isEmpty()) {
            leave.setStatus("Pending");
        }

        // Validate ApprovedBy user if provided
        if (leave.getApprovedBy() != null && leave.getApprovedBy().getUserId() != null) {
            User approvedBy = userRepo.findById(leave.getApprovedBy().getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Approved By User not found with ID: " + leave.getApprovedBy().getUserId()));
            leave.setApprovedBy(approvedBy);
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

        // Re-calculate days if dates changed
        if (leave.getStartDate() != null && leave.getEndDate() != null) {
            long days = ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate()) + 1;
            existingLeave.setTotalDays((int) days);
        }

        // Validate and update Employee
        if (leave.getEmployee() != null && leave.getEmployee().getEmpId() != null) {
            Employee employee = employeeRepo.findById(leave.getEmployee().getEmpId())
                    .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
            existingLeave.setEmployee(employee);
        }

        // Validate and update LeaveType
        if (leave.getLeaveType() != null && leave.getLeaveType().getLeaveTypeId() != null) {
            LeaveType leaveType = leaveTypeRepo.findById(leave.getLeaveType().getLeaveTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("Leave Type not found"));
            existingLeave.setLeaveType(leaveType);
        }

        existingLeave.setStartDate(leave.getStartDate());
        existingLeave.setEndDate(leave.getEndDate());
        existingLeave.setReason(leave.getReason());
        existingLeave.setStatus(leave.getStatus());

        // Update ApprovedBy if necessary
        if (leave.getApprovedBy() != null && leave.getApprovedBy().getUserId() != null) {
            User approvedBy = userRepo.findById(leave.getApprovedBy().getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Approved By User not found with ID: " + leave.getApprovedBy().getUserId()));
            existingLeave.setApprovedBy(approvedBy);
        }

        return employeeLeaveRepo.save(existingLeave);
    }
}