package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.TaxSlab;
import java.util.List;

public interface TaxSlabService {

    // Use Integer to match the taxSlabId type in the TaxSlab entity
    TaxSlab create(TaxSlab slab);

    TaxSlab update(Integer id, TaxSlab slab);

    void delete(Integer id);

    TaxSlab getById(Integer id);

    List<TaxSlab> getAll();
}