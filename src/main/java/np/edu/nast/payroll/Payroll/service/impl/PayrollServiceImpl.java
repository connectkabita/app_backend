package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.Payroll;
import np.edu.nast.payroll.Payroll.repository.PayrollRepository;
import np.edu.nast.payroll.Payroll.service.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PayrollServiceImpl implements PayrollService {

    @Autowired
    private PayrollRepository repository;

    @Override
    public Payroll savePayroll(Payroll payroll) {
        return repository.save(payroll);
    }

    @Override
    public List<Payroll> getAllPayrolls() {
        return repository.findAll();
    }

    @Override
    public Payroll getPayrollById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void deletePayroll(Integer id) {
        repository.deleteById(id);
    }
}
