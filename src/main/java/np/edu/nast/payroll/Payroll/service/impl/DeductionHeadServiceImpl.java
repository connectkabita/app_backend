package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.DeductionHead;
import np.edu.nast.payroll.Payroll.service.DeductionHeadService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeductionHeadServiceImpl implements DeductionHeadService {

    private List<DeductionHead> heads = new ArrayList<>();

    @Override
    public DeductionHead saveDeductionHead(DeductionHead head) {
        heads.add(head);
        return head;
    }

    @Override
    public List<DeductionHead> getAllDeductionHeads() {
        return heads;
    }

    @Override
    public DeductionHead getDeductionHeadById(Integer id) {
        return heads.stream().filter(h -> h.getDeductionHeadId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public void deleteDeductionHead(Integer id) {
        heads.removeIf(h -> h.getDeductionHeadId().equals(id));
    }
}
