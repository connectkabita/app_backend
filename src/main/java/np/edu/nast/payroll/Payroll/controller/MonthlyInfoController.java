package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.MonthlyInfo;
import np.edu.nast.payroll.Payroll.service.MonthlyInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/monthly-info")
public class MonthlyInfoController {

    @Autowired
    private MonthlyInfoService service;

    @PostMapping
    public MonthlyInfo createMonthlyInfo(@RequestBody MonthlyInfo info) {
        return service.saveMonthlyInfo(info);
    }

    @GetMapping
    public List<MonthlyInfo> getAllMonthlyInfos() {
        return service.getAllMonthlyInfos();
    }

    @GetMapping("/{id}")
    public MonthlyInfo getMonthlyInfo(@PathVariable Integer id) {
        return service.getMonthlyInfoById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteMonthlyInfo(@PathVariable Integer id) {
        service.deleteMonthlyInfo(id);
    }
}
