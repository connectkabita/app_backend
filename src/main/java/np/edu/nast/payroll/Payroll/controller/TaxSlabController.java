package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.TaxSlab;
import np.edu.nast.payroll.Payroll.service.TaxSlabService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tax-slabs")
@CrossOrigin(origins = "http://localhost:5173")
public class TaxSlabController {

    private final TaxSlabService service;

    public TaxSlabController(TaxSlabService service) {
        this.service = service;
    }

    @PostMapping
    public TaxSlab create(@RequestBody TaxSlab slab) {
        return service.create(slab);
    }

    @PutMapping("/{id}")
    public TaxSlab update(@PathVariable Integer id, @RequestBody TaxSlab slab) {
        return service.update(id, slab);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }

    @GetMapping("/{id}")
    public TaxSlab getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @GetMapping
    public List<TaxSlab> getAll() {
        return service.getAll();
    }
}
