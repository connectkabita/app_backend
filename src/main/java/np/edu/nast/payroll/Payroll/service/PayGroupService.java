package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.PayGroup;
import java.util.List;

public interface PayGroupService {
    PayGroup savePayGroup(PayGroup payGroup);
    List<PayGroup> getAllPayGroups();
    PayGroup getPayGroupById(Integer id);
    void deletePayGroup(Integer id);
}
