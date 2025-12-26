package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.EmployeeLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmployeeLeaveRepository extends JpaRepository<EmployeeLeave, Integer> {
    List<EmployeeLeave> findByEmployeeEmpId(Integer empId);
}