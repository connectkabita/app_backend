package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.SalaryGrade;
import java.util.List;

public interface SalaryGradeService {

    SalaryGrade create(SalaryGrade grade);
    SalaryGrade update(Long id, SalaryGrade grade);
    void delete(Long id);
    SalaryGrade getById(Long id);
    List<SalaryGrade> getAll();
}
