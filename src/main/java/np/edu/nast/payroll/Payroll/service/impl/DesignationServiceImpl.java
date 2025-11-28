package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.Designation;
import np.edu.nast.payroll.Payroll.service.DesignationService;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class DesignationServiceImpl implements DesignationService {

    private final List<Designation> designationList = new ArrayList<>();

    @Override
    public Designation saveDesignation(Designation designation) {
        designationList.add(designation);
        return designation;
    }

    @Override
    public Designation updateDesignation(Designation designation) {
        for (int i = 0; i < designationList.size(); i++) {
            if (designationList.get(i).getDesignationId().equals(designation.getDesignationId())) {
                designationList.set(i, designation);
                return designation;
            }
        }
        return null;
    }

    @Override
    public void deleteDesignation(Integer designationId) {
        designationList.removeIf(designation -> designation.getDesignationId().equals(designationId));
    }

    @Override
    public Designation getDesignationById(Integer designationId) {
        return designationList.stream()
                .filter(designation -> designation.getDesignationId().equals(designationId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Designation> getAllDesignations() {
        return designationList;
    }
}
