package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.TaxSlab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
// FIX: Changed generic ID type from Long to Integer to match Entity's taxSlabId
public interface TaxSlabRepository extends JpaRepository<TaxSlab, Integer> {

    /**
     * Filters slabs by "Single" or "Couple" and orders them.
     * Use this in your service: taxSlabRepo.findByTaxpayerStatusOrderByMinAmountAsc(status)
     */
    List<TaxSlab> findByTaxpayerStatusOrderByMinAmountAsc(String taxpayerStatus);

    /**
     * Retrieves all slabs regardless of status, ordered by min amount.
     */
    List<TaxSlab> findAllByOrderByMinAmountAsc();

    /**
     * Explicit JPQL version if you prefer manual queries.
     */
    @Query("SELECT t FROM TaxSlab t WHERE t.taxpayerStatus = :status ORDER BY t.minAmount ASC")
    List<TaxSlab> findSlabsByStatus(@Param("status") String status);
}