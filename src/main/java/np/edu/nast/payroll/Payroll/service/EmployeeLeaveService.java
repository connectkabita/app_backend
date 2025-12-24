package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.EmployeeLeave;

import java.util.List;

public interface EmployeeLeaveService {

    EmployeeLeave requestLeave(EmployeeLeave leave);

    EmployeeLeave updateLeave(Integer id, EmployeeLeave leave);

    void deleteLeave(Integer id);

    EmployeeLeave getLeaveById(Integer id);

    List<EmployeeLeave> getAllLeaves();

    List<EmployeeLeave> getLeavesByEmployee(Integer empId);
}
