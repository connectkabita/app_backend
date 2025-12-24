package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.EmployeeSalaryComponent;
import np.edu.nast.payroll.Payroll.repository.EmployeeSalaryComponentRepository;
import np.edu.nast.payroll.Payroll.service.EmployeeSalaryComponentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmployeeSalaryComponentServiceImpl implements EmployeeSalaryComponentService {

    private final EmployeeSalaryComponentRepository repo;

    public EmployeeSalaryComponentServiceImpl(EmployeeSalaryComponentRepository repo) {
        this.repo = repo;
    }

    /* =========================
       CREATE EMPLOYEE SALARY COMPONENT
       ========================= */
    @Override
    public EmployeeSalaryComponent create(EmployeeSalaryComponent esc) {
        if (esc.getEmployee() == null || esc.getEmployee().getEmpId() == null) {
            throw new IllegalArgumentException("Employee is required");
        }
        if (esc.getSalaryComponent() == null || esc.getSalaryComponent().getComponentId() == null) {
            throw new IllegalArgumentException("Salary Component is required");
        }

        return repo.save(esc);
    }

    /* =========================
       UPDATE EMPLOYEE SALARY COMPONENT
       ========================= */
    @Override
    public EmployeeSalaryComponent update(Integer id, EmployeeSalaryComponent esc) {
        EmployeeSalaryComponent existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("EmployeeSalaryComponent not found with ID: " + id));

        if (esc.getEmployee() == null || esc.getEmployee().getEmpId() == null) {
            throw new IllegalArgumentException("Employee is required");
        }
        if (esc.getSalaryComponent() == null || esc.getSalaryComponent().getComponentId() == null) {
            throw new IllegalArgumentException("Salary Component is required");
        }

        existing.setEmployee(esc.getEmployee());
        existing.setSalaryComponent(esc.getSalaryComponent());
        existing.setValue(esc.getValue());
        existing.setIsActive(esc.getIsActive());
        existing.setEffectiveFrom(esc.getEffectiveFrom());
        existing.setEffectiveTo(esc.getEffectiveTo());

        return repo.save(existing);
    }

    /* =========================
       DELETE EMPLOYEE SALARY COMPONENT
       ========================= */
    @Override
    public void delete(Integer id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("EmployeeSalaryComponent not found with ID: " + id);
        }
        repo.deleteById(id);
    }

    /* =========================
       GET BY ID
       ========================= */
    @Override
    public EmployeeSalaryComponent getById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("EmployeeSalaryComponent not found with ID: " + id));
    }

    /* =========================
       GET ALL
       ========================= */
    @Override
    public List<EmployeeSalaryComponent> getAll() {
        return repo.findAll();
    }
}
