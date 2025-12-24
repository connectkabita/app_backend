package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.DeductionHead;
import java.util.List;

public interface DeductionHeadService {
    DeductionHead saveDeductionHead(DeductionHead head);
    List<DeductionHead> getAllDeductionHeads();
    DeductionHead getDeductionHeadById(Integer id);
    DeductionHead updateDeductionHead(DeductionHead head);
    void deleteDeductionHead(Integer id);
}
