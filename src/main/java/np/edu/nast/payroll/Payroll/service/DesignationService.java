package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.Designation;
import java.util.List;

public interface DesignationService {

    Designation saveDesignation(Designation designation);

    Designation updateDesignation(Designation designation);

    void deleteDesignation(Integer designationId);

    Designation getDesignationById(Integer designationId);

    List<Designation> getAllDesignations();
}
