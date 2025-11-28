package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.EmployeeDocument;
import java.util.List;

public interface EmployeeDocumentService {
    EmployeeDocument saveDocument(EmployeeDocument document);
    List<EmployeeDocument> getAllDocuments();
    EmployeeDocument getDocumentById(Integer id);
    void deleteDocument(Integer id);
}
