package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.Employee;
import np.edu.nast.payroll.Payroll.entity.EmployeeLeave;
import np.edu.nast.payroll.Payroll.exception.ResourceNotFoundException;
import np.edu.nast.payroll.Payroll.repository.EmployeeLeaveRepository;
import np.edu.nast.payroll.Payroll.repository.EmployeeRepository;
import np.edu.nast.payroll.Payroll.service.EmployeeLeaveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmployeeLeaveServiceImpl implements EmployeeLeaveService {

    private final EmployeeLeaveRepository leaveRepository;
    private final EmployeeRepository employeeRepository;

    public EmployeeLeaveServiceImpl(EmployeeLeaveRepository leaveRepository,
                                    EmployeeRepository employeeRepository) {
        this.leaveRepository = leaveRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public EmployeeLeave requestLeave(EmployeeLeave leave) {
        if (leave.getEmployee() == null || leave.getEmployee().getEmpId() == null) {
            throw new IllegalArgumentException("Employee ID is required for leave request");
        }

        // Check if employee exists
        Employee employee = employeeRepository.findById(leave.getEmployee().getEmpId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with ID: " + leave.getEmployee().getEmpId()));

        leave.setEmployee(employee);
        return leaveRepository.save(leave);
    }

    @Override
    public EmployeeLeave updateLeave(Integer id, EmployeeLeave leave) {
        EmployeeLeave existing = leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found with ID: " + id));

        existing.setStartDate(leave.getStartDate());
        existing.setEndDate(leave.getEndDate());
        existing.setTotalDays(leave.getTotalDays());
        existing.setStatus(leave.getStatus());
        existing.setReason(leave.getReason());

        return leaveRepository.save(existing);
    }

    @Override
    public void deleteLeave(Integer id) {
        if (!leaveRepository.existsById(id)) {
            throw new ResourceNotFoundException("Leave not found with ID: " + id);
        }
        leaveRepository.deleteById(id);
    }

    @Override
    public EmployeeLeave getLeaveById(Integer id) {
        return leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found with ID: " + id));
    }

    @Override
    public List<EmployeeLeave> getAllLeaves() {
        return leaveRepository.findAll();
    }

    @Override
    public List<EmployeeLeave> getLeavesByEmployee(Integer empId) {
        if (!employeeRepository.existsById(empId)) {
            throw new ResourceNotFoundException("Employee not found with ID: " + empId);
        }
        return leaveRepository.findByEmployeeEmpId(empId);
    }
}
