package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.Bank;
import np.edu.nast.payroll.Payroll.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/banks")
public class BankController {

    @Autowired
    private BankService bankService;

    @PostMapping
    public Bank createBank(@RequestBody Bank bank) {
        return bankService.saveBank(bank);
    }

    @PutMapping("/{id}")
    public Bank updateBank(@PathVariable("id") Integer id, @RequestBody Bank bank) {
        bank.setBankId(id);
        return bankService.updateBank(bank);
    }

    @DeleteMapping("/{id}")
    public void deleteBank(@PathVariable("id") Integer id) {
        bankService.deleteBank(id);
    }

    @GetMapping("/{id}")
    public Bank getBankById(@PathVariable("id") Integer id) {
        return bankService.getBankById(id);
    }

    @GetMapping
    public List<Bank> getAllBanks() {
        return bankService.getAllBanks();
    }
}
