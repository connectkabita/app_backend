package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.TaxSlab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaxSlabRepository extends JpaRepository<TaxSlab, Long> {

    // Option A: Standard Query Method
    List<TaxSlab> findAllByOrderByMinAmountAsc();

    // Option B: Bulletproof JPQL version (Use this if A fails)
    @Query("SELECT t FROM TaxSlab t ORDER BY t.minAmount ASC")
    List<TaxSlab> findAllOrderedByMinAmount();
}