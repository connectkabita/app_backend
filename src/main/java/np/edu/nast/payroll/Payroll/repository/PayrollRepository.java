package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Integer> {


}