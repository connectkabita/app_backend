package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.MonthlyInfo;
import np.edu.nast.payroll.Payroll.service.MonthlyInfoService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MonthlyInfoServiceImpl implements MonthlyInfoService {

    private List<MonthlyInfo> infos = new ArrayList<>();

    @Override
    public MonthlyInfo saveMonthlyInfo(MonthlyInfo info) {
        infos.add(info);
        return info;
    }

    @Override
    public List<MonthlyInfo> getAllMonthlyInfos() {
        return infos;
    }

    @Override
    public MonthlyInfo getMonthlyInfoById(Integer id) {
        return infos.stream().filter(i -> i.getMonthlyInfoId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public void deleteMonthlyInfo(Integer id) {
        infos.removeIf(i -> i.getMonthlyInfoId().equals(id));
    }
}
