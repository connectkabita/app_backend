package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.SalaryGrade;
import np.edu.nast.payroll.Payroll.repository.SalaryGradeRepository;
import np.edu.nast.payroll.Payroll.service.SalaryGradeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalaryGradeServiceImpl implements SalaryGradeService {

    private final SalaryGradeRepository repo;

    public SalaryGradeServiceImpl(SalaryGradeRepository repo) {
        this.repo = repo;
    }

    @Override
    public SalaryGrade create(SalaryGrade grade) {
        return repo.save(grade);
    }

    @Override
    public SalaryGrade update(Long id, SalaryGrade grade) {
        SalaryGrade existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("SalaryGrade not found"));

        existing.setGradeName(grade.getGradeName());
        existing.setDescription(grade.getDescription());

        return repo.save(existing);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Override
    public SalaryGrade getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("SalaryGrade not found"));
    }

    @Override
    public List<SalaryGrade> getAll() {
        return repo.findAll();
    }
}
