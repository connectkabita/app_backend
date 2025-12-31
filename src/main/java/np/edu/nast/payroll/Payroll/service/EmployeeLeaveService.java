package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.EmployeeLeave;
import java.util.List;

public interface EmployeeLeaveService {
    List<EmployeeLeave> getAllLeaves();
    EmployeeLeave updateLeaveStatus(Integer id, String status, Integer adminId);
    EmployeeLeave requestLeave(EmployeeLeave leave);
    EmployeeLeave getLeaveById(Integer id);
    List<EmployeeLeave> getLeavesByEmployee(Integer empId);
    void deleteLeave(Integer id);
    EmployeeLeave updateLeave(Integer id, EmployeeLeave leave);
}