package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.EmployeeSalaryComponent;
import np.edu.nast.payroll.Payroll.repository.EmployeeSalaryComponentRepository;
import np.edu.nast.payroll.Payroll.service.EmployeeSalaryComponentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeSalaryComponentServiceImpl implements EmployeeSalaryComponentService {

    private final EmployeeSalaryComponentRepository repo;

    public EmployeeSalaryComponentServiceImpl(EmployeeSalaryComponentRepository repo) {
        this.repo = repo;
    }

    @Override
    public EmployeeSalaryComponent create(EmployeeSalaryComponent esc) {
        return repo.save(esc);
    }

    @Override
    public EmployeeSalaryComponent update(Long id, EmployeeSalaryComponent esc) {
        // FIX: Removed Math.toIntExact (assuming Repository ID is now Long)
        EmployeeSalaryComponent existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("EmployeeSalaryComponent not found"));

        existing.setEmployee(esc.getEmployee());

        // FIX 1: The field is named 'salaryComponent', so the getter/setter is getSalaryComponent/setSalaryComponent.
        existing.setSalaryComponent(esc.getSalaryComponent());

        existing.setValue(esc.getValue());

        // FIX 2: Lombok generates isActive() getter and setIsActive(Boolean) setter for Boolean fields.
        existing.setIsActive(esc.getIsActive());

        existing.setEffectiveFrom(esc.getEffectiveFrom());
        existing.setEffectiveTo(esc.getEffectiveTo());

        return repo.save(existing);
    }

    @Override
    public void delete(Long id) {
        // FIX 3: Removed Math.toIntExact (assuming Repository ID is now Long)
        repo.deleteById(id);
    }

    @Override
    public EmployeeSalaryComponent getById(Long id) {
        // FIX 4: Removed Math.toIntExact (assuming Repository ID is now Long)
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("EmployeeSalaryComponent not found"));
    }

    @Override
    public List<EmployeeSalaryComponent> getAll() {
        return repo.findAll();
    }
}