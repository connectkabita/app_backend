package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.Payroll;
import java.util.List;
import java.util.Map;

public interface PayrollService {
    List<Payroll> getAllPayrolls();
    // This fixes the red error in PayrollController
    Payroll processPayroll(Map<String, Object> payload);
    Payroll updateStatus(Integer id, String status);
}