package np.edu.nast.payroll.Payroll.service.impl;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.entity.Employee;
import np.edu.nast.payroll.Payroll.entity.EmployeeSalaryComponent;
import np.edu.nast.payroll.Payroll.entity.SalaryComponent;
import np.edu.nast.payroll.Payroll.repository.EmployeeRepository;
import np.edu.nast.payroll.Payroll.repository.EmployeeSalaryComponentRepository;
import np.edu.nast.payroll.Payroll.repository.SalaryComponentRepository;
import np.edu.nast.payroll.Payroll.service.EmployeeSalaryComponentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor // Automatically injects final fields via constructor
public class EmployeeSalaryComponentServiceImpl implements EmployeeSalaryComponentService {

    private final EmployeeSalaryComponentRepository repo;
    private final EmployeeRepository employeeRepository;
    private final SalaryComponentRepository componentRepository;

    @Override
    public EmployeeSalaryComponent create(EmployeeSalaryComponent esc) {
        // 1. Basic ID validation
        if (esc.getEmployee() == null || esc.getEmployee().getEmpId() == null) {
            throw new IllegalArgumentException("Employee ID is required");
        }
        if (esc.getSalaryComponent() == null || esc.getSalaryComponent().getComponentId() == null) {
            throw new IllegalArgumentException("Salary Component ID is required");
        }

        // 2. FETCH FULL DETAILS FROM DB (This prevents the NULL values in response)
        Employee fullEmployee = employeeRepository.findById(esc.getEmployee().getEmpId())
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + esc.getEmployee().getEmpId()));

        SalaryComponent fullComponent = componentRepository.findById(esc.getSalaryComponent().getComponentId())
                .orElseThrow(() -> new RuntimeException("Component not found with ID: " + esc.getSalaryComponent().getComponentId()));

        // 3. ATTACH FULL OBJECTS TO THE ENTITY
        esc.setEmployee(fullEmployee);
        esc.setSalaryComponent(fullComponent);

        // 4. Save and return (Now it contains all details)
        return repo.save(esc);
    }

    @Override
    public EmployeeSalaryComponent update(Integer id, EmployeeSalaryComponent esc) {
        EmployeeSalaryComponent existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("EmployeeSalaryComponent not found with ID: " + id));

        // Fetch full entities for the update as well
        Employee fullEmployee = employeeRepository.findById(esc.getEmployee().getEmpId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        SalaryComponent fullComponent = componentRepository.findById(esc.getSalaryComponent().getComponentId())
                .orElseThrow(() -> new RuntimeException("Component not found"));

        existing.setEmployee(fullEmployee);
        existing.setSalaryComponent(fullComponent);
        existing.setValue(esc.getValue());
        existing.setIsActive(esc.getIsActive());
        existing.setEffectiveFrom(esc.getEffectiveFrom());
        existing.setEffectiveTo(esc.getEffectiveTo());

        return repo.save(existing);
    }

    @Override
    public void delete(Integer id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("EmployeeSalaryComponent not found with ID: " + id);
        }
        repo.deleteById(id);
    }

    @Override
    public EmployeeSalaryComponent getById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));
    }

    @Override
    public List<EmployeeSalaryComponent> getAll() {
        return repo.findAll();
    }
}