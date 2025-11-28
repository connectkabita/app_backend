package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.PayoutInfo;
import np.edu.nast.payroll.Payroll.service.PayoutInfoService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PayoutInfoServiceImpl implements PayoutInfoService {

    private List<PayoutInfo> payouts = new ArrayList<>();

    @Override
    public PayoutInfo savePayoutInfo(PayoutInfo payout) {
        payouts.add(payout);
        return payout;
    }

    @Override
    public List<PayoutInfo> getAllPayoutInfos() {
        return payouts;
    }

    @Override
    public PayoutInfo getPayoutInfoById(Integer id) {
        return payouts.stream().filter(p -> p.getPayoutId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public void deletePayoutInfo(Integer id) {
        payouts.removeIf(p -> p.getPayoutId().equals(id));
    }
}
