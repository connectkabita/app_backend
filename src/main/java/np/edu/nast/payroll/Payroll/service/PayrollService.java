package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.Payroll;
import np.edu.nast.payroll.Payroll.dto.auth.PayrollRequest;
import java.util.List;

public interface PayrollService {
    List<Payroll> getAllPayrolls();
    Payroll savePayroll(Payroll payroll);
    Payroll processPayrollRequest(PayrollRequest request);

    // Updated to Integer to match Implementation and Repo
    Payroll getPayrollById(Integer id);
    void deletePayroll(Integer id);

    // NEW: The method required by the Controller
    Payroll updateStatus(Integer id, String newStatus);
}