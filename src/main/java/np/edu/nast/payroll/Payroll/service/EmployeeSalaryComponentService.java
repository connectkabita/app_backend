package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.EmployeeSalaryComponent;
import java.util.List;

public interface EmployeeSalaryComponentService {

    EmployeeSalaryComponent create(EmployeeSalaryComponent esc);
    EmployeeSalaryComponent update(Long id, EmployeeSalaryComponent esc);
    void delete(Long id);
    EmployeeSalaryComponent getById(Long id);
    List<EmployeeSalaryComponent> getAll();
}
