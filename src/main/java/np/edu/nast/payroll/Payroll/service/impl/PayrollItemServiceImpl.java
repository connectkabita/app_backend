package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.PayrollItem;
import np.edu.nast.payroll.Payroll.repository.PayrollItemRepository;
import np.edu.nast.payroll.Payroll.service.PayrollItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PayrollItemServiceImpl implements PayrollItemService {

    @Autowired
    private PayrollItemRepository repository;

    @Override
    public PayrollItem savePayrollItem(PayrollItem item) {
        return repository.save(item);
    }

    @Override
    public List<PayrollItem> getAllPayrollItems() {
        return repository.findAll();
    }

    @Override
    public PayrollItem getPayrollItemById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void deletePayrollItem(Integer id) {
        repository.deleteById(id);
    }
}
