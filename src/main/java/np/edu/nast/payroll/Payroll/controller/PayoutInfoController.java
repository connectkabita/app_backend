package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.PayoutInfo;
import np.edu.nast.payroll.Payroll.service.PayoutInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payout-info")
public class PayoutInfoController {

    @Autowired
    private PayoutInfoService service;

    @PostMapping
    public PayoutInfo createPayout(@RequestBody PayoutInfo payout) {
        return service.savePayoutInfo(payout);
    }

    @GetMapping
    public List<PayoutInfo> getAllPayouts() {
        return service.getAllPayoutInfos();
    }

    @GetMapping("/{id}")
    public PayoutInfo getPayout(@PathVariable Integer id) {
        return service.getPayoutInfoById(id);
    }

    @DeleteMapping("/{id}")
    public void deletePayout(@PathVariable Integer id) {
        service.deletePayoutInfo(id);
    }
}
