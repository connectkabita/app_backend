package np.edu.nast.payroll.Payroll.service.impl;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.entity.SalaryComponent;
import np.edu.nast.payroll.Payroll.repository.SalaryComponentRepository;
import np.edu.nast.payroll.Payroll.service.SalaryComponentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor // Automatically creates constructor for 'repo'
public class SalaryComponentServiceImpl implements SalaryComponentService {

    private final SalaryComponentRepository repo;

    @Override
    @Transactional
    public SalaryComponent create(SalaryComponent component) {
        // Validation: Logic now focuses on the component itself
        if (component.getComponentName() == null || component.getComponentName().isEmpty()) {
            throw new RuntimeException("Component name is required");
        }
        return repo.save(component);
    }

    @Override
    @Transactional
    public SalaryComponent update(Long id, SalaryComponent component) {
        SalaryComponent existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Salary component not found with ID: " + id));

        // Update fields directly without foreign key lookups
        existing.setComponentName(component.getComponentName());
        existing.setCalculationMethod(component.getCalculationMethod());
        existing.setDefaultValue(component.getDefaultValue());
        existing.setDescription(component.getDescription());
        existing.setRequired(component.isRequired());

        return repo.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Cannot delete: Component not found");
        }
        repo.deleteById(id);
    }

    @Override
    public SalaryComponent getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Salary component not found"));
    }

    @Override
    public List<SalaryComponent> getAll() {
        return repo.findAll();
    }
}