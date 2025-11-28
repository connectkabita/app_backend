package np.edu.nast.payroll.Payroll.repository;
import np.edu.nast.payroll.Payroll.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
public interface DepartmentRepository extends JpaRepository<Department, Integer> {}
