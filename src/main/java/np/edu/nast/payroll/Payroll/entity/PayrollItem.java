package np.edu.nast.payroll.Payroll.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payroll_item")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PayrollItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer payrollItemId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payroll_id",  nullable = false)
    private Payroll payroll;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "component_id",  nullable = false)
    private SalaryComponent component;
    @Column(nullable = false)
    private Double amount;
    @Column(nullable = false)
    private String itemType;
    @Column(nullable = false)
    private String notes;
}
