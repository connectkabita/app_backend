package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.EmployeeDocument;
import np.edu.nast.payroll.Payroll.service.EmployeeDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee-documents")
public class EmployeeDocumentController {

    @Autowired
    private EmployeeDocumentService service;

    @PostMapping
    public EmployeeDocument createDocument(@RequestBody EmployeeDocument document) {
        return service.saveDocument(document);
    }

    @GetMapping
    public List<EmployeeDocument> getAllDocuments() {
        return service.getAllDocuments();
    }

    @GetMapping("/{id}")
    public EmployeeDocument getDocument(@PathVariable Integer id) {
        return service.getDocumentById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteDocument(@PathVariable Integer id) {
        service.deleteDocument(id);
    }
}
