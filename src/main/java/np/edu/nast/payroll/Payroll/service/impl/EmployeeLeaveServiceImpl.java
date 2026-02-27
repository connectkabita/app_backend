package np.edu.nast.payroll.Payroll.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import np.edu.nast.payroll.Payroll.entity.*;
import np.edu.nast.payroll.Payroll.repository.*;
import np.edu.nast.payroll.Payroll.service.EmployeeLeaveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j // Fixed: Added to resolve "cannot find symbol: variable log"
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
    public List<EmployeeLeave> getFilteredLeaves(Integer year, Integer month, String status, String search) {
        String searchTerm = (search == null) ? "" : search;
        String statusFilter = (status == null) ? "All" : status;
        return employeeLeaveRepo.findFilteredLeaves(year, month, statusFilter, searchTerm);
    }

    @Override
    @Transactional
    public EmployeeLeave updateLeaveStatus(Integer id, String status, Integer adminId, String rejectionReason) {
        EmployeeLeave leave = employeeLeaveRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave record not found with ID: " + id));

        String oldStatus = leave.getStatus();

        // Business Logic: Only update balance if status actually changes to/from Approved
        if ("Approved".equalsIgnoreCase(status) && !"Approved".equalsIgnoreCase(oldStatus)) {
            updateActualBalance(leave, true); // Deduct days
        }
        else if ("Rejected".equalsIgnoreCase(status) && "Approved".equalsIgnoreCase(oldStatus)) {
            updateActualBalance(leave, false); // Refund days
        }

        leave.setStatus(status);

        // Fixed: Finding the User account linked to the Admin Employee
        Employee adminEmployee = employeeRepo.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin Employee not found ID: " + adminId));

        User adminUser = adminEmployee.getUser(); // Ensure this mapping exists in Employee entity
        if (adminUser == null) {
            log.error("Admin Employee {} has no linked User account", adminId);
            throw new RuntimeException("Admin account linkage error.");
        }

        leave.setApprovedBy(adminUser);
        leave.setApprovedAt(LocalDateTime.now());
        leave.setRejectionReason("Rejected".equalsIgnoreCase(status) ? rejectionReason : null);

        return employeeLeaveRepo.save(leave);
    }

    private void updateActualBalance(EmployeeLeave leave, boolean isDeducting) {
        int year = leave.getStartDate().getYear();
        LeaveBalance balance = leaveBalanceRepo.findByEmployeeAndLeaveTypeAndYear(
                leave.getEmployee(),
                leave.getLeaveType(),
                year
        ).orElseThrow(() -> new RuntimeException("Leave balance not initialized for " +
                leave.getEmployee().getEmpId() + " for year " + year));

        double totalDays = (double) leave.getTotalDays();

        if (isDeducting) {
            if (balance.getCurrentBalanceDays() < totalDays) {
                throw new RuntimeException("Insufficient balance. Available: " + balance.getCurrentBalanceDays());
            }
            balance.setCurrentBalanceDays(balance.getCurrentBalanceDays() - totalDays);
        } else {
            balance.setCurrentBalanceDays(balance.getCurrentBalanceDays() + totalDays);
        }
        leaveBalanceRepo.save(balance);
    }

    @Override
    @Transactional
    public EmployeeLeave requestLeave(EmployeeLeave leave) {
        Employee employee = employeeRepo.findById(leave.getEmployee().getEmpId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        LeaveType leaveType = leaveTypeRepo.findById(leave.getLeaveType().getLeaveTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Leave Type not found"));

        leave.setEmployee(employee);
        leave.setLeaveType(leaveType);

        calculateAndSetTotalDays(leave);

        if (leave.getStatus() == null) leave.setStatus("Pending");

        return employeeLeaveRepo.save(leave);
    }

    private void calculateAndSetTotalDays(EmployeeLeave leave) {
        if (leave.getStartDate() != null && leave.getEndDate() != null) {
            long days = ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate()) + 1;
            if (days <= 0) throw new IllegalArgumentException("End date must be after start date");
            leave.setTotalDays((int) days);
        }
    }

    @Override public EmployeeLeave getLeaveById(Integer id) { return employeeLeaveRepo.findById(id).orElse(null); }
    @Override public void deleteLeave(Integer id) { employeeLeaveRepo.deleteById(id); }

    @Override
    public List<EmployeeLeave> getLeavesByEmployee(Integer empId) {
        Employee emp = employeeRepo.findById(empId).orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        return employeeLeaveRepo.findAllByEmployee(emp);
    }

    @Override @Transactional
    public EmployeeLeave updateLeave(Integer id, EmployeeLeave leave) {
        EmployeeLeave existing = employeeLeaveRepo.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        existing.setStartDate(leave.getStartDate());
        existing.setEndDate(leave.getEndDate());
        existing.setReason(leave.getReason());
        calculateAndSetTotalDays(existing);
        return employeeLeaveRepo.save(existing);
    }
}