package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.SalaryComponent;
import java.util.List;

public interface SalaryComponentService {

    SalaryComponent create(SalaryComponent component);
    SalaryComponent update(Long id, SalaryComponent component);
    void delete(Long id);
    SalaryComponent getById(Long id);
    List<SalaryComponent> getAll();
}
