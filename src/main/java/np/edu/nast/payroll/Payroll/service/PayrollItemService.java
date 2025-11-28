package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.PayrollItem;
import java.util.List;

public interface PayrollItemService {
    PayrollItem savePayrollItem(PayrollItem item);
    List<PayrollItem> getAllPayrollItems();
    PayrollItem getPayrollItemById(Integer id);
    void deletePayrollItem(Integer id);
}
