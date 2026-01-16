package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.EmployeeSalaryComponent;
import np.edu.nast.payroll.Payroll.entity.SalaryComponent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SalaryComponentRepository extends JpaRepository<SalaryComponent, Long> {


}
