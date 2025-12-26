package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.EmployeeLeave;
import java.util.List;

public interface EmployeeLeaveService {
    EmployeeLeave requestLeave(EmployeeLeave leave);
    List<EmployeeLeave> getAllLeaves();
    EmployeeLeave getLeaveById(Integer id);
    List<EmployeeLeave> getLeavesByEmployee(Integer empId);
    void deleteLeave(Integer id);
    EmployeeLeave updateLeave(Integer id, EmployeeLeave leave);

    // UPDATED: Added adminId to capture who is performing the action
    EmployeeLeave updateLeaveStatus(Integer id, String status, Integer adminId);
}