package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.EmployeeSalaryComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmployeeSalaryComponentRepository extends JpaRepository<EmployeeSalaryComponent, Integer> {

    // Fetches all active pay components (Allowances/Deductions) for an employee
    List<EmployeeSalaryComponent> findByEmployeeEmpIdAndIsActiveTrue(Integer empId);
}