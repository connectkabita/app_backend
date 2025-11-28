package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.MonthlyInfo;
import java.util.List;

public interface MonthlyInfoService {
    MonthlyInfo saveMonthlyInfo(MonthlyInfo info);
    List<MonthlyInfo> getAllMonthlyInfos();
    MonthlyInfo getMonthlyInfoById(Integer id);
    void deleteMonthlyInfo(Integer id);
}
