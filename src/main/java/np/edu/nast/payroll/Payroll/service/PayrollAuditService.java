package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.PayrollAudit;
import java.util.List;

public interface PayrollAuditService {
    PayrollAudit savePayrollAudit(PayrollAudit audit);
    List<PayrollAudit> getAllPayrollAudits();
    PayrollAudit getPayrollAuditById(Integer id);
    void deletePayrollAudit(Integer id);
}
