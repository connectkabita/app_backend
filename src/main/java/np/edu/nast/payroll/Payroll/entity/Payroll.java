package np.edu.nast.payroll.Payroll.entity;

import com.fasterxml.jackson.annotation.JsonProperty; // FIXED: Added missing import
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "payroll",
        indexes = {
                @Index(name = "idx_payroll_emp", columnList = "emp_id"),
                @Index(name = "idx_payroll_pay_date", columnList = "pay_date")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payroll {

    // Matches 'EMPLOYEE NAME' column in frontend
    @JsonProperty("employeeName")
    public String getEmployeeName() {
        return this.employee != null ? this.employee.getFirstName() + " " + this.employee.getLastName() : "N/A";
    }

    // Matches 'id' used for verification operations
    @JsonProperty("id")
    public Integer getId() {
        return this.payrollId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payroll_id")
    private Integer payrollId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "processed_by", nullable = false)
    private User processedBy;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "payment_account_id", nullable = false)
    private BankAccount paymentAccount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pay_group_id", nullable = true)
    private PayGroup payGroup;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "payment_method_id", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "pay_period_start")
    private LocalDate payPeriodStart;

    @Column(name = "pay_period_end")
    private LocalDate payPeriodEnd;

    @Column(name = "pay_date")
    private LocalDate payDate;

    @Column(nullable = false)
    private Double grossSalary = 0.0;

    @Column(nullable = false)
    private Double totalAllowances = 0.0;

    @Column(nullable = false)
    private Double totalDeductions = 0.0;

    @Column(nullable = false)
    private Double totalTax = 0.0;

    @Column(nullable = false)
    private Double netSalary = 0.0;

    @Column(nullable = false, length = 20)
    private String status;

    @Builder.Default
    @Column(name = "processed_at", nullable = false, updatable = false)
    private LocalDateTime processedAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        if (this.processedAt == null) {
            this.processedAt = LocalDateTime.now();
        }
    }
}