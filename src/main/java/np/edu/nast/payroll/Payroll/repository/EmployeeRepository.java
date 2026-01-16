package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    @Query("SELECT FUNCTION('MONTH', e.joiningDate) as month, COUNT(e) " +
            "FROM Employee e " +
            "WHERE e.isActive = true " +
            "GROUP BY FUNCTION('MONTH', e.joiningDate)")
    List<Object[]> countActiveEmployeesPerMonth();

    boolean existsByEmail(String email);

    // NEW: Search by ID or Name (Case-Insensitive)
    @Query("SELECT e FROM Employee e WHERE " +
            "CAST(e.empId AS string) = :query OR " +
            "LOWER(e.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(e.lastName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Employee> searchByIdOrName(@Param("query") String query);

    Optional<Employee> findByEmail(String email);

}