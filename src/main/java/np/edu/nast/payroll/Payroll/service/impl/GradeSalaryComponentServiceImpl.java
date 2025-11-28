package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.GradeSalaryComponent;
import np.edu.nast.payroll.Payroll.repository.GradeSalaryComponentRepository;
import np.edu.nast.payroll.Payroll.service.GradeSalaryComponentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GradeSalaryComponentServiceImpl implements GradeSalaryComponentService {

    private final GradeSalaryComponentRepository repo;

    public GradeSalaryComponentServiceImpl(GradeSalaryComponentRepository repo) {
        this.repo = repo;
    }

    @Override
    public GradeSalaryComponent create(GradeSalaryComponent gsc) {
        return repo.save(gsc);
    }

    @Override
    public GradeSalaryComponent update(Long id, GradeSalaryComponent gsc) {
        GradeSalaryComponent existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("GradeSalaryComponent not found"));

        existing.setGrade(gsc.getGrade());
        existing.setComponent(gsc.getComponent());
        existing.setValue(gsc.getValue());

        return repo.save(existing);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Override
    public GradeSalaryComponent getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("GradeSalaryComponent not found"));
    }

    @Override
    public List<GradeSalaryComponent> getAll() {
        return repo.findAll();
    }
}
