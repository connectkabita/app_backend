package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.DeductionHead;
import np.edu.nast.payroll.Payroll.exception.ResourceNotFoundException;
import np.edu.nast.payroll.Payroll.repository.DeductionHeadRepository;
import np.edu.nast.payroll.Payroll.service.DeductionHeadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DeductionHeadServiceImpl implements DeductionHeadService {

    private final DeductionHeadRepository repository;

    public DeductionHeadServiceImpl(DeductionHeadRepository repository) {
        this.repository = repository;
    }

    @Override
    public DeductionHead saveDeductionHead(DeductionHead head) {
        if (head == null) {
            throw new IllegalArgumentException("DeductionHead cannot be null");
        }
        return repository.save(head);
    }

    @Override
    public List<DeductionHead> getAllDeductionHeads() {
        return repository.findAll();
    }

    @Override
    public DeductionHead getDeductionHeadById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "DeductionHead not found with ID: " + id));
    }

    @Override
    public DeductionHead updateDeductionHead(DeductionHead head) {
        if (head == null || head.getDeductionHeadId() == null) {
            throw new IllegalArgumentException("DeductionHead ID is required for update");
        }

        DeductionHead existing = repository.findById(head.getDeductionHeadId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "DeductionHead not found with ID: " + head.getDeductionHeadId()));

        existing.setName(head.getName());
        existing.setDescription(head.getDescription());

        return repository.save(existing);
    }

    @Override
    public void deleteDeductionHead(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("DeductionHead not found with ID: " + id);
        }
        repository.deleteById(id);
    }
}
