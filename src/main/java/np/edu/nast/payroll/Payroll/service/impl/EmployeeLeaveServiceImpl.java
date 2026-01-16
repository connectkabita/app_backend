package np.edu.nast.payroll.Payroll.service.impl;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.entity.*;
import np.edu.nast.payroll.Payroll.repository.*;
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
    private final LeaveBalanceRepository leaveBalanceRepo;

    @Override
    public List<EmployeeLeave> getAllLeaves() {
        return employeeLeaveRepo.findAll();
    }

    @Override
    @Transactional // Ensures the status update and quota deduction happen together
    public EmployeeLeave updateLeaveStatus(Integer id, String status, Integer adminId) {
        EmployeeLeave leave = employeeLeaveRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave record not found with ID: " + id));

        // Logic for "Approved" status
        if ("Approved".equalsIgnoreCase(status) && !"Approved".equalsIgnoreCase(leave.getStatus())) {

            // 1. Fetch the specific balance for this Employee and this Leave Type
            LeaveBalance balance = leaveBalanceRepo.findByEmployeeEmpIdAndLeaveTypeLeaveTypeId(
                    leave.getEmployee().getEmpId(),
                    leave.getLeaveType().getLeaveTypeId()
            ).orElseThrow(() -> new RuntimeException("No leave balance record found for this employee and leave type."));

            double currentBalance = balance.getCurrentBalanceDays();
            double daysToDeduct = (leave.getTotalDays() != null) ? leave.getTotalDays() : 0;

            // 2. Validate Quota
            if (currentBalance < daysToDeduct) {
                throw new RuntimeException("Insufficient leave balance! Available: " + currentBalance + ", Requested: " + daysToDeduct);
            }

            // 3. Deduct from Available Quota
            balance.setCurrentBalanceDays(currentBalance - daysToDeduct);
            leaveBalanceRepo.save(balance);

            // 4. Record Approval Details
            User admin = userRepo.findById(adminId)
                    .orElseThrow(() -> new IllegalArgumentException("Admin User not found with ID: " + adminId));
            leave.setApprovedBy(admin);
            leave.setApprovedAt(LocalDateTime.now());

        } else if ("Rejected".equalsIgnoreCase(status)) {
            leave.setApprovedBy(null);
            leave.setApprovedAt(null);
        }

        // 5. Update and Save Leave Status
        leave.setStatus(status);
        return employeeLeaveRepo.save(leave);
    }

    @Override
    @Transactional
    public EmployeeLeave requestLeave(EmployeeLeave leave) {
        if (leave.getEmployee() == null || leave.getEmployee().getEmpId() == null) {
            throw new IllegalArgumentException("Employee ID is required");
        }
        Employee employee = employeeRepo.findById(leave.getEmployee().getEmpId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        leave.setEmployee(employee);

        if (leave.getLeaveType() == null || leave.getLeaveType().getLeaveTypeId() == null) {
            throw new IllegalArgumentException("Leave Type ID is required");
        }
        LeaveType leaveType = leaveTypeRepo.findById(leave.getLeaveType().getLeaveTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Leave Type not found"));
        leave.setLeaveType(leaveType);

        // Ensure total days are calculated before saving
        calculateAndSetTotalDays(leave);

        if (leave.getStatus() == null || leave.getStatus().isEmpty()) {
            leave.setStatus("Pending");
        }

        return employeeLeaveRepo.save(leave);
    }

    @Override
    public List<EmployeeLeave> getLeavesByEmployee(Integer empId) {
        Employee employee = employeeRepo.findById(empId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        return employeeLeaveRepo.findAllByEmployee(employee);
    }

    @Override
    public EmployeeLeave getLeaveById(Integer id) {
        return employeeLeaveRepo.findById(id).orElse(null);
    }

    @Override
    public void deleteLeave(Integer id) {
        EmployeeLeave leave = employeeLeaveRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Leave not found"));
        employeeLeaveRepo.delete(leave);
    }

    @Override
    @Transactional
    public EmployeeLeave updateLeave(Integer id, EmployeeLeave leave) {
        EmployeeLeave existingLeave = employeeLeaveRepo.findById(id).orElseThrow();
        existingLeave.setStartDate(leave.getStartDate());
        existingLeave.setEndDate(leave.getEndDate());
        calculateAndSetTotalDays(existingLeave);
        existingLeave.setReason(leave.getReason());
        existingLeave.setStatus(leave.getStatus());
        return employeeLeaveRepo.save(existingLeave);
    }

    private void calculateAndSetTotalDays(EmployeeLeave leave) {
        if (leave.getStartDate() != null && leave.getEndDate() != null) {
            // +1 to include both start and end dates
            long days = ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate()) + 1;
            if (days <= 0) {
                throw new IllegalArgumentException("End date must be after start date");
            }
            leave.setTotalDays((int) days);
        }
    }
}