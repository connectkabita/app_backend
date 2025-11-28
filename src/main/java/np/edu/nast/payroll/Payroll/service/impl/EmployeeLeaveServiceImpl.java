package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.EmployeeLeave;
import np.edu.nast.payroll.Payroll.repository.EmployeeLeaveRepository;
import np.edu.nast.payroll.Payroll.service.EmployeeLeaveService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeLeaveServiceImpl implements EmployeeLeaveService {

    private final EmployeeLeaveRepository employeeLeaveRepository;

    public EmployeeLeaveServiceImpl(EmployeeLeaveRepository repo) {
        this.employeeLeaveRepository = repo;
    }

    @Override
    public EmployeeLeave requestLeave(EmployeeLeave leave) {
        return employeeLeaveRepository.save(leave);
    }

    @Override
    public EmployeeLeave updateLeave(Long id, EmployeeLeave leave) {
        EmployeeLeave existing = employeeLeaveRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new RuntimeException("Leave not found"));

        existing.setStartDate(leave.getStartDate());
        existing.setEndDate(leave.getEndDate());
        existing.setTotalDays(leave.getTotalDays());
        existing.setStatus(leave.getStatus());
        existing.setReason(leave.getReason());

        return employeeLeaveRepository.save(existing);
    }

    @Override
    public void deleteLeave(Long id) {
        employeeLeaveRepository.deleteById(Math.toIntExact(id));
    }

    @Override
    public EmployeeLeave getLeaveById(Long id) {
        return employeeLeaveRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new RuntimeException("Leave not found"));
    }

    @Override
    public List<EmployeeLeave> getAllLeaves() {
        return employeeLeaveRepository.findAll();
    }

    @Override
    public List<EmployeeLeave> getLeavesByEmployee(Long empId) {
        return employeeLeaveRepository.findByEmployeeEmpId(empId);
    }
}
