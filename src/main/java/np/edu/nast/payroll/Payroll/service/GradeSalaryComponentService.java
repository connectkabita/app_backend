package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.GradeSalaryComponent;
import java.util.List;

public interface GradeSalaryComponentService {

    GradeSalaryComponent create(GradeSalaryComponent gsc);
    GradeSalaryComponent update(Long id, GradeSalaryComponent gsc);
    void delete(Long id);
    GradeSalaryComponent getById(Long id);
    List<GradeSalaryComponent> getAll();
}
