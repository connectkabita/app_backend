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
            // Validate and fetch the admin user who is performing the approval
            User admin = userRepo.findById(adminId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Admin User not found with ID: " + adminId));

            leave.setApprovedBy(admin); // Updates the approved_by_user_id column
            leave.setApprovedAt(LocalDateTime.now()); // Updates the approved_at column
        } else {
            // If rejected or pending, clear approval data
            leave.setApprovedBy(null);
            leave.setApprovedAt(null);
        }

        return employeeLeaveRepo.save(leave);
    }

    @Override
    @Transactional
    public EmployeeLeave requestLeave(EmployeeLeave leave) {

        // Validate Employee
        if (leave.getEmployee() == null || leave.getEmployee().getEmpId() == null) {
            throw new IllegalArgumentException("Employee ID is required");
        }
        Employee employee = employeeRepo.findById(leave.getEmployee().getEmpId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Employee not found with ID: " + leave.getEmployee().getEmpId()));
        leave.setEmployee(employee);

        // Validate LeaveType
        if (leave.getLeaveType() == null || leave.getLeaveType().getLeaveTypeId() == null) {
            throw new IllegalArgumentException("Leave Type ID is required");
        }
        LeaveType leaveType = leaveTypeRepo.findById(leave.getLeaveType().getLeaveTypeId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Leave Type not found with ID: " + leave.getLeaveType().getLeaveTypeId()));
        leave.setLeaveType(leaveType);

        // Validate ApprovedBy user if provided
        if (leave.getApprovedBy() != null && leave.getApprovedBy().getUserId() != null) {
            User approvedBy = userRepo.findById(leave.getApprovedBy().getUserId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Approved By User not found with ID: " + leave.getApprovedBy().getUserId()));
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

        // Validate Employee
        if (leave.getEmployee() == null || leave.getEmployee().getEmpId() == null) {
            throw new IllegalArgumentException("Employee ID is required");
        }
        Employee employee = employeeRepo.findById(leave.getEmployee().getEmpId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Employee not found with ID: " + leave.getEmployee().getEmpId()));
        existingLeave.setEmployee(employee);

        // Validate LeaveType
        if (leave.getLeaveType() == null || leave.getLeaveType().getLeaveTypeId() == null) {
            throw new IllegalArgumentException("Leave Type ID is required");
        }
        LeaveType leaveType = leaveTypeRepo.findById(leave.getLeaveType().getLeaveTypeId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Leave Type not found with ID: " + leave.getLeaveType().getLeaveTypeId()));
        existingLeave.setLeaveType(leaveType);

        // Copy other fields
        existingLeave.setStartDate(leave.getStartDate());
        existingLeave.setEndDate(leave.getEndDate());
        existingLeave.setTotalDays(leave.getTotalDays());
        existingLeave.setReason(leave.getReason());
        existingLeave.setStatus(leave.getStatus());

        // Validate ApprovedBy if provided
        if (leave.getApprovedBy() != null && leave.getApprovedBy().getUserId() != null) {
            User approvedBy = userRepo.findById(leave.getApprovedBy().getUserId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Approved By User not found with ID: " + leave.getApprovedBy().getUserId()));
            existingLeave.setApprovedBy(approvedBy);
        } else {
            existingLeave.setApprovedBy(null);
        }

        return employeeLeaveRepo.save(existingLeave);
    }
}
