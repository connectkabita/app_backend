package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.PayGroup;
import np.edu.nast.payroll.Payroll.repository.PayGroupRepository;
import np.edu.nast.payroll.Payroll.service.PayGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PayGroupServiceImpl implements PayGroupService {

    @Autowired
    private PayGroupRepository repository;

    @Override
    public PayGroup savePayGroup(PayGroup payGroup) {
        return repository.save(payGroup);
    }

    @Override
    public List<PayGroup> getAllPayGroups() {
        return repository.findAll();
    }

    @Override
    public PayGroup getPayGroupById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void deletePayGroup(Integer id) {
        repository.deleteById(id);
    }
}
