package np.edu.nast.payroll.Payroll.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payroll_item")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PayrollItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer payrollItemId;

    @ManyToOne
    @JoinColumn(name = "payroll_id")
    private Payroll payroll;

    @ManyToOne
    @JoinColumn(name = "component_id")
    private SalaryComponent component;

    private Double amount;
    private String itemType;
    private String notes;
}
