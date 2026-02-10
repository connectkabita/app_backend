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
    @Column(nullable = false)
    private Integer monthlyInfoId;
    @Column(nullable = false)
    private String monthName;
    @Column(nullable = false)
    private LocalDate monthStart;
    @Column(nullable = false)
    private LocalDate monthEnd;

    @ManyToOne
    @JoinColumn(name = "pay_group_id",  nullable = false)
    private PayGroup payGroup;
    @Column(nullable = false)
    private Integer totalEmployeesProcessed;
    @Column(nullable = false)
    private Double totalGrossSalary;
    @Column(nullable = false)
    private Double totalAllowances;
    @Column(nullable = false)
    private Double totalDeductions;
    @Column(nullable = false)
    private Double totalTax;
    @Column(nullable = false)
    private Double totalNetSalary;
    @Column(nullable = false)
    private String currency;
    @Column(nullable = false)
    private String status;

    @ManyToOne(fetch = FetchType.EAGER) // Ensure the user is loaded
    @JoinColumn(name = "generated_by", nullable = false)
    private User generatedBy;
    @Column(nullable = false, updatable = false)
    private LocalDateTime generatedAt;
}
