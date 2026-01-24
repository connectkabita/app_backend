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

    // Matches the naming convention: Entity 'employee' -> Field 'empId'
    List<Payroll> findByEmployeeEmpId(Integer empId);

    @Query("SELECT COALESCE(SUM(p.netSalary), 0) FROM Payroll p WHERE YEAR(p.payDate) = :year AND p.status != 'VOIDED'")
    double yearlyPayroll(@Param("year") int year);

    @Query("SELECT COALESCE(SUM(p.totalDeductions), 0) FROM Payroll p WHERE YEAR(p.payDate) = :year AND p.status != 'VOIDED'")
    double yearlyDeductions(@Param("year") int year);

    @Query("SELECT COALESCE(SUM(p.totalAllowances), 0) FROM Payroll p WHERE YEAR(p.payDate) = :year AND p.status != 'VOIDED'")
    double yearlyAllowances(@Param("year") int year);

    @Query("""
        SELECT new np.edu.nast.payroll.Payroll.reportdto.MonthlyPayrollDTO(
            FUNCTION('MONTHNAME', p.payDate), SUM(p.netSalary)
        )
        FROM Payroll p
        WHERE FUNCTION('YEAR', p.payDate) = :year AND p.status != 'VOIDED'
        GROUP BY FUNCTION('MONTH', p.payDate), FUNCTION('MONTHNAME', p.payDate)
        ORDER BY FUNCTION('MONTH', p.payDate)
    """)
    List<MonthlyPayrollDTO> monthlyPayroll(@Param("year") int year);

    @Query("SELECT p FROM Payroll p WHERE p.employee.empId = :empId AND YEAR(p.payDate) = :year AND MONTH(p.payDate) = :month")
    Optional<Payroll> findByEmployeeEmpIdAndMonth(@Param("empId") Integer empId, @Param("year") int year, @Param("month") int month);
}