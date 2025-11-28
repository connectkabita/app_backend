package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.PayrollAudit;
import np.edu.nast.payroll.Payroll.service.PayrollAuditService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PayrollAuditServiceImpl implements PayrollAuditService {

    private List<PayrollAudit> audits = new ArrayList<>();

    @Override
    public PayrollAudit savePayrollAudit(PayrollAudit audit) {
        audits.add(audit);
        return audit;
    }

    @Override
    public List<PayrollAudit> getAllPayrollAudits() {
        return audits;
    }

    @Override
    public PayrollAudit getPayrollAuditById(Integer id) {
        return audits.stream().filter(a -> a.getAuditId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public void deletePayrollAudit(Integer id) {
        audits.removeIf(a -> a.getAuditId().equals(id));
    }
}
