package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.TaxSlab;
import java.util.List;

public interface TaxSlabService {

    TaxSlab create(TaxSlab slab);
    TaxSlab update(Long id, TaxSlab slab);
    void delete(Long id);
    TaxSlab getById(Long id);
    List<TaxSlab> getAll();
}
