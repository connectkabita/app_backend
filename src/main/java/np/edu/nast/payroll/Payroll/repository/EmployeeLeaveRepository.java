package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.Employee;
import np.edu.nast.payroll.Payroll.entity.EmployeeLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmployeeLeaveRepository extends JpaRepository<EmployeeLeave, Integer> {

    // Required by your Service implementation to fix the "Cannot resolve method" error
    List<EmployeeLeave> findAllByEmployee(Employee employee);

    // Filter by ID directly if needed
    List<EmployeeLeave> findByEmployeeEmpId(Integer empId);

    // New line added for status counting
    long countByStatus(String status);
}