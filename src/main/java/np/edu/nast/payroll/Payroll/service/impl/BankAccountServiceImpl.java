package np.edu.nast.payroll.Payroll.service.impl;
import np.edu.nast.payroll.Payroll.entity.BankAccount;
import np.edu.nast.payroll.Payroll.repository.BankAccountRepository;
import np.edu.nast.payroll.Payroll.service.BankAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository repo;
    public BankAccountServiceImpl(BankAccountRepository repo){ this.repo = repo; }

    @Override public BankAccount create(BankAccount account){ return repo.save(account); }
    @Override public BankAccount update(Integer id, BankAccount account){
        BankAccount ex = repo.findById(id).orElseThrow(() -> new RuntimeException("BankAccount not found"));
        ex.setAccountNumber(account.getAccountNumber());
        ex.setAccountType(account.getAccountType());
        ex.setIsPrimary(account.getIsPrimary());
        ex.setBank(account.getBank());
        ex.setCurrency(account.getCurrency());
        return repo.save(ex);
    }
    @Override public void delete(Integer id){ repo.deleteById(id); }
    @Override public BankAccount getById(Integer id){ return repo.findById(id).orElseThrow(() -> new RuntimeException("BankAccount not found")); }
    @Override public List<BankAccount> getAll(){ return repo.findAll(); }
    @Override public List<BankAccount> findByEmployeeId(Integer empId){ return repo.findByEmployeeEmpId(empId); }
}
