package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.SalarySlip;
import java.util.List;

public interface SalarySlipService {
    SalarySlip saveSalarySlip(SalarySlip slip);
    List<SalarySlip> getAllSalarySlips();
    SalarySlip getSalarySlipById(Integer id);
    void deleteSalarySlip(Integer id);
}
