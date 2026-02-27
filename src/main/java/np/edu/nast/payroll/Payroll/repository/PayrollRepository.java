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

    @Query("SELECT p FROM Payroll p WHERE YEAR(p.payPeriodStart) = :year AND MONTH(p.payPeriodStart) = :month")
    List<Payroll> findByMonthAndYear(@Param("month") int month, @Param("year") int year);

    List<Payroll> findByEmployeeEmpId(Integer empId);

    @Query("SELECT COALESCE(SUM(p.netSalary), 0) FROM Payroll p WHERE YEAR(p.payPeriodStart) = :year AND p.status != 'VOIDED'")
    double yearlyPayroll(@Param("year") int year);

    @Query("SELECT COALESCE(SUM(p.totalDeductions), 0) FROM Payroll p WHERE YEAR(p.payPeriodStart) = :year AND p.status != 'VOIDED'")
    double yearlyDeductions(@Param("year") int year);

    @Query("SELECT COALESCE(SUM(p.totalAllowances), 0) FROM Payroll p WHERE YEAR(p.payPeriodStart) = :year AND p.status != 'VOIDED'")
    double yearlyAllowances(@Param("year") int year);

    /**
     * UPDATED FOR TOTAL GROSS:
     * We added SUM(p.grossSalary) to the selection.
     * This allows the Controller to populate the Gross Pay card with live DB values.
     */
    @Query("""
        SELECT d.deptName, SUM(p.netSalary), SUM(p.totalTax), SUM(p.grossSalary)
        FROM Payroll p
        JOIN p.employee e
        JOIN e.department d
        WHERE p.monthlyInfo.monthlyInfoId = :monthlyInfoId
        GROUP BY d.deptId, d.deptName
    """)
    List<Object[]> getDepartmentalTotals(@Param("monthlyInfoId") Integer monthlyInfoId);

    @Query("""
        SELECT new np.edu.nast.payroll.Payroll.reportdto.MonthlyPayrollDTO(
            FUNCTION('MONTHNAME', p.payPeriodStart), SUM(p.netSalary)
        )
        FROM Payroll p
        WHERE FUNCTION('YEAR', p.payPeriodStart) = :year AND p.status != 'VOIDED'
        GROUP BY FUNCTION('MONTH', p.payPeriodStart), FUNCTION('MONTHNAME', p.payPeriodStart)
        ORDER BY FUNCTION('MONTH', p.payPeriodStart)
    """)
    List<MonthlyPayrollDTO> monthlyPayroll(@Param("year") int year);

    @Query("SELECT p FROM Payroll p WHERE p.employee.empId = :empId AND YEAR(p.payPeriodStart) = :year AND MONTH(p.payPeriodStart) = :month")
    Optional<Payroll> findByEmployeeEmpIdAndMonth(@Param("empId") Integer empId, @Param("year") int year, @Param("month") int month);
}