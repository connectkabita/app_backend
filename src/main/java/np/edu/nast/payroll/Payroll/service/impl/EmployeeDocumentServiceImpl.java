package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.EmployeeDocument;
import np.edu.nast.payroll.Payroll.service.EmployeeDocumentService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeDocumentServiceImpl implements EmployeeDocumentService {

    private List<EmployeeDocument> documents = new ArrayList<>();

    @Override
    public EmployeeDocument saveDocument(EmployeeDocument document) {
        documents.add(document);
        return document;
    }

    @Override
    public List<EmployeeDocument> getAllDocuments() {
        return documents;
    }

    @Override
    public EmployeeDocument getDocumentById(Integer id) {
        return documents.stream().filter(d -> d.getDocumentId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public void deleteDocument(Integer id) {
        documents.removeIf(d -> d.getDocumentId().equals(id));
    }
}
