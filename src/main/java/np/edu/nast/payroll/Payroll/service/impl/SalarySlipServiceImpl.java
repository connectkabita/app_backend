package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.SalarySlip;
import np.edu.nast.payroll.Payroll.service.SalarySlipService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SalarySlipServiceImpl implements SalarySlipService {

    private List<SalarySlip> salarySlips = new ArrayList<>();

    @Override
    public SalarySlip saveSalarySlip(SalarySlip slip) {
        salarySlips.add(slip);
        return slip;
    }

    @Override
    public List<SalarySlip> getAllSalarySlips() {
        return salarySlips;
    }

    @Override
    public SalarySlip getSalarySlipById(Integer id) {
        return salarySlips.stream().filter(s -> s.getSlipId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public void deleteSalarySlip(Integer id) {
        salarySlips.removeIf(s -> s.getSlipId().equals(id));
    }
}
