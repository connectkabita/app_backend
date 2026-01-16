package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.salaryDTO.EmployeeSalaryAnalyticsDTO;

import java.time.YearMonth;

public interface SalaryAnalyticsService {

    EmployeeSalaryAnalyticsDTO getSalaryForUser(String username, YearMonth yearMonth);

}
