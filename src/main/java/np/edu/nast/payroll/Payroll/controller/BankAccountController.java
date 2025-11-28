package np.edu.nast.payroll.Payroll.controller;
import np.edu.nast.payroll.Payroll.entity.BankAccount;
import np.edu.nast.payroll.Payroll.service.BankAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bank-accounts")
public class BankAccountController {
    private final BankAccountService svc;
    public BankAccountController(BankAccountService svc){ this.svc = svc; }

    @PostMapping @ResponseStatus(HttpStatus.CREATED) public BankAccount create(@RequestBody BankAccount b){ return svc.create(b); }
    @GetMapping public List<BankAccount> getAll(){ return svc.getAll(); }
    @GetMapping("/{id}") public BankAccount getById(@PathVariable Integer id){ return svc.getById(id); }
    @GetMapping("/employee/{empId}") public List<BankAccount> byEmp(@PathVariable Integer empId){ return svc.findByEmployeeId(empId); }
    @PutMapping("/{id}") public BankAccount update(@PathVariable Integer id,@RequestBody BankAccount b){ return svc.update(id,b); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Integer id){ svc.delete(id); }
}
