package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.PayrollItem;
import np.edu.nast.payroll.Payroll.service.PayrollItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payroll-items")
public class PayrollItemController {

    @Autowired
    private PayrollItemService service;

    @PostMapping
    public PayrollItem createPayrollItem(@RequestBody PayrollItem item) {
        return service.savePayrollItem(item);
    }

    @GetMapping
    public List<PayrollItem> getAllPayrollItems() {
        return service.getAllPayrollItems();
    }

    @GetMapping("/{id}")
    public PayrollItem getPayrollItem(@PathVariable Integer id) {
        return service.getPayrollItemById(id);
    }

    @DeleteMapping("/{id}")
    public void deletePayrollItem(@PathVariable Integer id) {
        service.deletePayrollItem(id);
    }
}
