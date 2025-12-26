package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveTypeRepository extends JpaRepository<LeaveType, Integer> {
}