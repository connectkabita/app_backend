package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.Payroll;
import np.edu.nast.payroll.Payroll.dto.auth.PayrollRequest;
import java.util.List;

public interface PayrollService {
    List<Payroll> getAllPayrolls();
    Payroll savePayroll(Payroll payroll);

    // Fixes the "@Override" error in image_1f2b5d.jpg
    Payroll processPayrollRequest(PayrollRequest request);

    Payroll getPayrollById(Integer id);
    void deletePayroll(Integer id);
}