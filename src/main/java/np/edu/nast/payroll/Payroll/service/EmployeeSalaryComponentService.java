package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.EmployeeSalaryComponent;
import java.util.List;

public interface EmployeeSalaryComponentService {

    EmployeeSalaryComponent create(EmployeeSalaryComponent esc);
    EmployeeSalaryComponent update(Integer id, EmployeeSalaryComponent esc);
    void delete(Integer id);
    EmployeeSalaryComponent getById(Integer id);
    List<EmployeeSalaryComponent> getAll();
}
