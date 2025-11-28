package np.edu.nast.payroll.Payroll.service;
import np.edu.nast.payroll.Payroll.entity.Department;
import java.util.List;
public interface DepartmentService {
    Department create(Department dept);
    Department update(Integer id, Department dept);
    void delete(Integer id);
    Department getById(Integer id);
    List<Department> getAll();
}
