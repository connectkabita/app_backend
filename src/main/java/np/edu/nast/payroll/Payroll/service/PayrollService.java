package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.Payroll;
import java.util.List;

public interface PayrollService {
    Payroll savePayroll(Payroll payroll);
    List<Payroll> getAllPayrolls();
    Payroll getPayrollById(Integer id);
    void deletePayroll(Integer id);
}
