package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.Bank;
import np.edu.nast.payroll.Payroll.service.BankService;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class BankServiceImpl implements BankService {

    private final List<Bank> bankList = new ArrayList<>();

    @Override
    public Bank saveBank(Bank bank) {
        bankList.add(bank);
        return bank;
    }

    @Override
    public Bank updateBank(Bank bank) {
        for (int i = 0; i < bankList.size(); i++) {
            if (bankList.get(i).getBankId().equals(bank.getBankId())) {
                bankList.set(i, bank);
                return bank;
            }
        }
        return null;
    }

    @Override
    public void deleteBank(Integer bankId) {
        bankList.removeIf(bank -> bank.getBankId().equals(bankId));
    }

    @Override
    public Bank getBankById(Integer bankId) {
        return bankList.stream()
                .filter(bank -> bank.getBankId().equals(bankId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Bank> getAllBanks() {
        return bankList;
    }
}
