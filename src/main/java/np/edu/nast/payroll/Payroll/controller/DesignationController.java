package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.Designation;
import np.edu.nast.payroll.Payroll.service.DesignationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/designations")
public class DesignationController {

    @Autowired
    private DesignationService designationService;

    @PostMapping
    public Designation createDesignation(@RequestBody Designation designation) {
        return designationService.saveDesignation(designation);
    }

    @PutMapping("/{id}")
    public Designation updateDesignation(@PathVariable("id") Integer id, @RequestBody Designation designation) {
        designation.setDesignationId(id);
        return designationService.updateDesignation(designation);
    }

    @DeleteMapping("/{id}")
    public void deleteDesignation(@PathVariable("id") Integer id) {
        designationService.deleteDesignation(id);
    }

    @GetMapping("/{id}")
    public Designation getDesignationById(@PathVariable("id") Integer id) {
        return designationService.getDesignationById(id);
    }

    @GetMapping
    public List<Designation> getAllDesignations() {
        return designationService.getAllDesignations();
    }
}
