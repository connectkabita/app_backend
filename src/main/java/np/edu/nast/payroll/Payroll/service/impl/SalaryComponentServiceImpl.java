package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.SalaryComponent;
import np.edu.nast.payroll.Payroll.repository.SalaryComponentRepository;
import np.edu.nast.payroll.Payroll.service.SalaryComponentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalaryComponentServiceImpl implements SalaryComponentService {

    private final SalaryComponentRepository repo;

    public SalaryComponentServiceImpl(SalaryComponentRepository repo) {
        this.repo = repo;
    }

    @Override
    public SalaryComponent create(SalaryComponent component) {
        return repo.save(component);
    }

    @Override
    public SalaryComponent update(Long id, SalaryComponent component) {
        SalaryComponent existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("SalaryComponent not found"));

        existing.setComponentName(component.getComponentName());
        existing.setComponentType(component.getComponentType());
        existing.setCalculationMethod(component.getCalculationMethod());
        existing.setDefaultValue(component.getDefaultValue());
        existing.setDescription(component.getDescription());
        existing.setRequired(component.isRequired());

        return repo.save(existing);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Override
    public SalaryComponent getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("SalaryComponent not found"));
    }

    @Override
    public List<SalaryComponent> getAll() {
        return repo.findAll();
    }
}
