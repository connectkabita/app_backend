package np.edu.nast.payroll.Payroll.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "monthly_info")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MonthlyInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer monthlyInfoId;

    private String monthName;
    private LocalDate monthStart;
    private LocalDate monthEnd;

    @ManyToOne
    @JoinColumn(name = "pay_group_id")
    private PayGroup payGroup;

    private Integer totalEmployeesProcessed;
    private Double totalGrossSalary;
    private Double totalAllowances;
    private Double totalDeductions;
    private Double totalTax;
    private Double totalNetSalary;
    private String currency;
    private String status;

    @ManyToOne
    @JoinColumn(name = "generated_by")
    private User generatedBy;

    private LocalDateTime generatedAt;
}
