package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.SalaryComponentType;
import java.util.List;

public interface SalaryComponentTypeService {

    SalaryComponentType create(SalaryComponentType type);
    SalaryComponentType update(Long id, SalaryComponentType type);
    void delete(Long id);
    SalaryComponentType getById(Long id);
    List<SalaryComponentType> getAll();
}
