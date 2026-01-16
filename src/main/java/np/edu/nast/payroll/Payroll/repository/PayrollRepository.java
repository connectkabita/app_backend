package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.Payroll;
import np.edu.nast.payroll.Payroll.reportdto.MonthlyPayrollDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
    SELECT new np.edu.nast.payroll.Payroll.reportdto.MonthlyPayrollDTO(
        FUNCTION('MONTHNAME', p.payDate), SUM(p.netSalary)
    )
    FROM Payroll p
    WHERE FUNCTION('YEAR', p.payDate) = :year
    GROUP BY FUNCTION('MONTH', p.payDate), FUNCTION('MONTHNAME', p.payDate)
    ORDER BY FUNCTION('MONTH', p.payDate)
""")
    List<MonthlyPayrollDTO> monthlyPayroll(@Param("year") int year);


    @Query("""
       SELECT p
       FROM Payroll p
       JOIN p.employee e
       LEFT JOIN e.bankAccounts b ON b.isPrimary = TRUE
       WHERE e.id = :empId
         AND FUNCTION('DATE_FORMAT', p.payDate, '%Y') = :year
         AND FUNCTION('DATE_FORMAT', p.payDate, '%m') = :month
       """)
    Optional<Payroll> findPayrollForMonth(
            @Param("empId") Integer empId,
            @Param("year") String year,
            @Param("month") String month
    );

}

