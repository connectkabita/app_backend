package np.edu.nast.payroll.Payroll.controller;
import np.edu.nast.payroll.Payroll.entity.PaymentMethod;
import np.edu.nast.payroll.Payroll.service.PaymentMethodService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payment-methods")
public class PaymentMethodController {
    private final PaymentMethodService svc;
    public PaymentMethodController(PaymentMethodService svc){ this.svc = svc; }

    @PostMapping @ResponseStatus(HttpStatus.CREATED) public PaymentMethod create(@RequestBody PaymentMethod p){ return svc.create(p); }
    @GetMapping public List<PaymentMethod> getAll(){ return svc.getAll(); }
    @GetMapping("/{id}") public PaymentMethod getById(@PathVariable Integer id){ return svc.getById(id); }
    @PutMapping("/{id}") public PaymentMethod update(@PathVariable Integer id,@RequestBody PaymentMethod p){ return svc.update(id,p); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Integer id){ svc.delete(id); }
}
