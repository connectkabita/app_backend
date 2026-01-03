package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.Employee;
import np.edu.nast.payroll.Payroll.reportdto.MonthlyPayrollDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface ReportRepository extends JpaRepository<Employee, String> {

    @Query("SELECT COUNT(e) FROM Employee e")
    long countEmployees();

    @Query("""
        SELECT COALESCE(SUM(p.netSalary), 0)
        FROM Payroll p
        WHERE YEAR(p.payDate) = :year
    """)
    double sumPayroll(int year);

    @Query("""
        SELECT COALESCE(SUM(p.totalDeductions), 0)
        FROM Payroll p
        WHERE YEAR(p.payDate) = :year
    """)
    double sumDeductions(int year);

    @Query("""
        SELECT COALESCE(SUM(p.totalAllowances), 0)
        FROM Payroll p
        WHERE YEAR(p.payDate) = :year
    """)
    double sumAllowances(int year);

   /* @Query("""
        SELECT COUNT(l)
        FROM LeaveRequest l
        WHERE l.status = 'PENDING'
    """)
    long countPendingLeaves();*/

    @Query("""
        SELECT new np.edu.nast.payroll.Payroll.reportdto.MonthlyPayrollDTO(
            FUNCTION('MONTHNAME', p.payDate),
            SUM(p.netSalary)
        )
        FROM Payroll p
        WHERE YEAR(p.payDate) = :year
        GROUP BY MONTH(p.payDate)
        ORDER BY MONTH(p.payDate)
    """)
    List<MonthlyPayrollDTO> monthlyPayroll(int year);
}
