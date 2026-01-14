package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Integer> { // Changed to Integer

    @Query("SELECT SUM(p.grossSalary) FROM Payroll p")
    Double sumTotalGross();

    @Query("SELECT SUM(p.totalDeductions) FROM Payroll p")
    Double sumTotalDeductions();

    @Query("SELECT SUM(p.netSalary) FROM Payroll p")
    Double sumTotalNet();

    long countByStatus(String status);
}