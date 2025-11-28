package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.DeductionHead;
import np.edu.nast.payroll.Payroll.service.DeductionHeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deduction-heads")
public class DeductionHeadController {

    @Autowired
    private DeductionHeadService service;

    @PostMapping
    public DeductionHead createDeductionHead(@RequestBody DeductionHead head) {
        return service.saveDeductionHead(head);
    }

    @GetMapping
    public List<DeductionHead> getAllDeductionHeads() {
        return service.getAllDeductionHeads();
    }

    @GetMapping("/{id}")
    public DeductionHead getDeductionHead(@PathVariable Integer id) {
        return service.getDeductionHeadById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteDeductionHead(@PathVariable Integer id) {
        service.deleteDeductionHead(id);
    }
}
