package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.Payroll;
import java.util.List;
import java.util.Map;

public interface PayrollService {
    List<Payroll> getAllPayrolls();
    Payroll processPayroll(Map<String, Object> payload);
    Payroll updateStatus(Integer id, String status);

    // Define these to fix Controller compilation errors
    List<Payroll> getPayrollByEmployeeId(Integer empId);
    Payroll voidPayroll(Integer id);
}