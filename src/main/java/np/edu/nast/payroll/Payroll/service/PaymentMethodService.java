package np.edu.nast.payroll.Payroll.service;
import np.edu.nast.payroll.Payroll.entity.PaymentMethod;
import java.util.List;
public interface PaymentMethodService {
    PaymentMethod create(PaymentMethod p);
    PaymentMethod update(Integer id, PaymentMethod p);
    void delete(Integer id);
    PaymentMethod getById(Integer id);
    List<PaymentMethod> getAll();
}
