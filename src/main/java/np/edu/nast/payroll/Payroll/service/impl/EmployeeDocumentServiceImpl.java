package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.EmployeeDocument;
import np.edu.nast.payroll.Payroll.entity.Employee;
import np.edu.nast.payroll.Payroll.exception.ResourceNotFoundException;
import np.edu.nast.payroll.Payroll.repository.EmployeeDocumentRepository;
import np.edu.nast.payroll.Payroll.repository.EmployeeRepository;
import np.edu.nast.payroll.Payroll.service.EmployeeDocumentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmployeeDocumentServiceImpl implements EmployeeDocumentService {

    private final EmployeeDocumentRepository documentRepository;
    private final EmployeeRepository employeeRepository;

    public EmployeeDocumentServiceImpl(EmployeeDocumentRepository documentRepository,
                                       EmployeeRepository employeeRepository) {
        this.documentRepository = documentRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public EmployeeDocument saveDocument(EmployeeDocument document) {
        if (document.getEmployee() == null || document.getEmployee().getEmpId() == null) {
            throw new IllegalArgumentException("Employee ID must not be null");
        }

        // Check if employee exists
        Employee employee = employeeRepository.findById(document.getEmployee().getEmpId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with ID: " + document.getEmployee().getEmpId()));

        document.setEmployee(employee);
        return documentRepository.save(document);
    }

    @Override
    public List<EmployeeDocument> getAllDocuments() {
        return documentRepository.findAll();
    }

    @Override
    public EmployeeDocument getDocumentById(Integer id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + id));
    }

    @Override
    public void deleteDocument(Integer id) {
        if (!documentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Document not found with ID: " + id);
        }
        documentRepository.deleteById(id);
    }
}
