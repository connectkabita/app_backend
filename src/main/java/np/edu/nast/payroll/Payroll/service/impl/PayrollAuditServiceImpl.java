package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.Payroll;
import np.edu.nast.payroll.Payroll.entity.PayrollAudit;
import np.edu.nast.payroll.Payroll.entity.User;
import np.edu.nast.payroll.Payroll.repository.PayrollAuditRepository;
import np.edu.nast.payroll.Payroll.repository.PayrollRepository;
import np.edu.nast.payroll.Payroll.repository.UserRepository;
import np.edu.nast.payroll.Payroll.service.PayrollAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PayrollAuditServiceImpl implements PayrollAuditService {

    @Autowired
    private PayrollAuditRepository auditRepository;

    @Autowired
    private PayrollRepository payrollRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public PayrollAudit savePayrollAudit(PayrollAudit audit) {
        // Fetch managed Payroll
        if (audit.getPayroll() != null && audit.getPayroll().getPayrollId() != null) {
            Payroll payroll = payrollRepository.findById(audit.getPayroll().getPayrollId())
                    .orElseThrow(() -> new RuntimeException("Payroll not found"));
            audit.setPayroll(payroll);
        } else {
            throw new RuntimeException("Payroll is required");
        }

        // Fetch managed User
        if (audit.getChangedBy() != null && audit.getChangedBy().getUserId() != null) {
            User user = userRepository.findById(audit.getChangedBy().getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            audit.setChangedBy(user);
        } else {
            throw new RuntimeException("ChangedBy (User) is required");
        }

        // Set timestamp
        audit.setChangedAt(LocalDateTime.now());

        return auditRepository.save(audit);
    }

    @Override
    public List<PayrollAudit> getAllPayrollAudits() {
        return auditRepository.findAll();
    }

    @Override
    public PayrollAudit getPayrollAuditById(Integer id) {
        return auditRepository.findById(Long.valueOf(id)).orElse(null);
    }

    @Override
    public void deletePayrollAudit(Integer id) {
        auditRepository.deleteById(Long.valueOf(id));
    }
}
