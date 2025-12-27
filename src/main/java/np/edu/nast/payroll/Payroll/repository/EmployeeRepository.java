package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    // Count active employees grouped by month
    @Query("SELECT FUNCTION('MONTH', e.joiningDate) as month, COUNT(e) " +
            "FROM Employee e " +
            "WHERE e.isActive = true " +
            "GROUP BY FUNCTION('MONTH', e.joiningDate)")
    List<Object[]> countActiveEmployeesPerMonth();
    boolean existsByEmail(String email);

}
