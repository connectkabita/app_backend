package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.Employee;
import np.edu.nast.payroll.Payroll.entity.EmployeeLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmployeeLeaveRepository extends JpaRepository<EmployeeLeave, Integer> {

    // Retrieves all leave records for a specific Employee object
    List<EmployeeLeave> findAllByEmployee(Employee employee);

    // Retrieves all leave records using just the Employee ID
    List<EmployeeLeave> findByEmployeeEmpId(Integer empId);

    // Counts how many leaves are in a certain status (e.g., "Pending")
    long countByStatus(String status);
}