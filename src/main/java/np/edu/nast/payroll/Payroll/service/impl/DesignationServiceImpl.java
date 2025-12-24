package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.Designation;
import np.edu.nast.payroll.Payroll.exception.ResourceNotFoundException;
import np.edu.nast.payroll.Payroll.repository.DesignationRepository;
import np.edu.nast.payroll.Payroll.service.DesignationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DesignationServiceImpl implements DesignationService {

    private final DesignationRepository designationRepository;

    public DesignationServiceImpl(DesignationRepository designationRepository) {
        this.designationRepository = designationRepository;
    }

    @Override
    public Designation saveDesignation(Designation designation) {
        if (designation == null || designation.getDesignationTitle() == null || designation.getDesignationTitle().isBlank()) {
            throw new IllegalArgumentException("Designation title must not be empty");
        }
        return designationRepository.save(designation);
    }

    @Override
    public Designation updateDesignation(Designation designation) {
        if (designation == null || designation.getDesignationId() == null) {
            throw new IllegalArgumentException("Designation ID is required for update");
        }
        if (designation.getDesignationTitle() == null || designation.getDesignationTitle().isBlank()) {
            throw new IllegalArgumentException("Designation title must not be empty");
        }

        Designation existing = designationRepository.findById(designation.getDesignationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Designation not found with ID: " + designation.getDesignationId()));

        existing.setDesignationTitle(designation.getDesignationTitle());

        return designationRepository.save(existing);
    }

    @Override
    public void deleteDesignation(Integer designationId) {
        if (!designationRepository.existsById(designationId)) {
            throw new ResourceNotFoundException("Designation not found with ID: " + designationId);
        }
        designationRepository.deleteById(designationId);
    }

    @Override
    public Designation getDesignationById(Integer designationId) {
        return designationRepository.findById(designationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Designation not found with ID: " + designationId));
    }

    @Override
    public List<Designation> getAllDesignations() {
        return designationRepository.findAll();
    }
}
