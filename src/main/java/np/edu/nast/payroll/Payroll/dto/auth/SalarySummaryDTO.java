package np.edu.nast.payroll.Payroll.dto.auth;
import java.util.List;

public class SalarySummaryDTO {
    public double totalGross;
    public double totalDeductions;
    public double totalNet;
    public List<DeptBreakdown> departments;

    public static class DeptBreakdown {
        public String name;
        public double net;
        public double tax;

        public DeptBreakdown(String name, double net, double tax) {
            this.name = name;
            this.net = net;
            this.tax = tax;
        }
    }
}