package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.PayoutInfo;
import java.util.List;

public interface PayoutInfoService {
    PayoutInfo savePayoutInfo(PayoutInfo payout);
    List<PayoutInfo> getAllPayoutInfos();
    PayoutInfo getPayoutInfoById(Integer id);
    void deletePayoutInfo(Integer id);
}
