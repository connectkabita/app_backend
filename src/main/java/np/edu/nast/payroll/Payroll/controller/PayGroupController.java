package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.PayGroup;
import np.edu.nast.payroll.Payroll.service.PayGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paygroups")
public class PayGroupController {

    @Autowired
    private PayGroupService service;

    @PostMapping
    public PayGroup createPayGroup(@RequestBody PayGroup payGroup) {
        return service.savePayGroup(payGroup);
    }

    @GetMapping
    public List<PayGroup> getAllPayGroups() {
        return service.getAllPayGroups();
    }

    @GetMapping("/{id}")
    public PayGroup getPayGroup(@PathVariable Integer id) {
        return service.getPayGroupById(id);
    }

    @DeleteMapping("/{id}")
    public void deletePayGroup(@PathVariable Integer id) {
        service.deletePayGroup(id);
    }
}
