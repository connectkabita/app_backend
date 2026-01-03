package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.dtoReports.MonthlyPayrollDTO;
import np.edu.nast.payroll.Payroll.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Integer> {

    @Query("""
        SELECT COALESCE(SUM(p.netSalary),0)
        FROM Payroll p
        WHERE YEAR(p.payDate)=:year
    """)
    double yearlyPayroll(int year);

    @Query("""
        SELECT COALESCE(SUM(p.totalDeductions),0)
        FROM Payroll p
        WHERE YEAR(p.payDate)=:year
    """)
    double yearlyDeductions(int year);

    @Query("""
        SELECT COALESCE(SUM(p.totalAllowances),0)
        FROM Payroll p
        WHERE YEAR(p.payDate)=:year
    """)
    double yearlyAllowances(int year);




    @Query("""
    SELECT new np.edu.nast.payroll.Payroll.dtoReports.MonthlyPayrollDTO(
        FUNCTION('MONTHNAME', p.payDate), SUM(p.netSalary)
    )
    FROM Payroll p
    WHERE FUNCTION('YEAR', p.payDate) = :year
    GROUP BY FUNCTION('MONTH', p.payDate), FUNCTION('MONTHNAME', p.payDate)
    ORDER BY FUNCTION('MONTH', p.payDate)
""")
    List<MonthlyPayrollDTO> monthlyPayroll(@Param("year") int year);












}