package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.EmployeeLeave;

import java.util.List;

public interface EmployeeLeaveService {
    EmployeeLeave requestLeave(EmployeeLeave leave);
    EmployeeLeave updateLeave(Long id, EmployeeLeave leave);
    void deleteLeave(Long id);
    EmployeeLeave getLeaveById(Long id);
    List<EmployeeLeave> getAllLeaves();
    List<EmployeeLeave> getLeavesByEmployee(Long empId);
}
