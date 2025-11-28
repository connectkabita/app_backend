package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.Employee;
import java.util.List;

public interface EmployeeService {
    Employee create(Employee employee);
    Employee update(Integer id, Employee employee);
    void delete(Integer id);
    Employee getById(Integer id);
    List<Employee> getAll();
}
