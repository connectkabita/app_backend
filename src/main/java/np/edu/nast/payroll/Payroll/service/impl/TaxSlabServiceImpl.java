package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.TaxSlab;
import np.edu.nast.payroll.Payroll.repository.TaxSlabRepository;
import np.edu.nast.payroll.Payroll.service.TaxSlabService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaxSlabServiceImpl implements TaxSlabService {

    private final TaxSlabRepository repo;

    public TaxSlabServiceImpl(TaxSlabRepository repo) {
        this.repo = repo;
    }

    @Override
    public TaxSlab create(TaxSlab slab) {
        return repo.save(slab);
    }

    @Override
    public TaxSlab update(Integer id, TaxSlab slab) {
        TaxSlab existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("TaxSlab not found"));

        existing.setName(slab.getName());
        existing.setMinAmount(slab.getMinAmount());
        existing.setMaxAmount(slab.getMaxAmount());
        existing.setRatePercentage(slab.getRatePercentage());
        existing.setEffectiveFrom(slab.getEffectiveFrom());
        existing.setEffectiveTo(slab.getEffectiveTo());
        existing.setDescription(slab.getDescription());
        existing.setTaxpayerStatus(slab.getTaxpayerStatus()); // Map the new field

        return repo.save(existing);
    }

    @Override
    public void delete(Integer id) {
        repo.deleteById(id);
    }

    @Override
    public TaxSlab getById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("TaxSlab not found"));
    }

    @Override
    public List<TaxSlab> getAll() {
        return repo.findAll();
    }
}
