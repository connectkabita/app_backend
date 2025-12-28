package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.PayGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayGroupRepository extends JpaRepository<PayGroup, Integer> {
}