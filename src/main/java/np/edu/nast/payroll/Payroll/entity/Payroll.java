package np.edu.nast.payroll.Payroll.entity;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer payrollId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "processed_by", nullable = false)
    private User processedBy;

    /* =========================================================
       FIX: Changed "account_id" to "payment_account_id"
       to match your MySQL table's mandatory column name.
       ========================================================= */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "payment_account_id", nullable = false)
    private BankAccount paymentAccount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pay_group_id", nullable = true)
    private PayGroup payGroup;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "payment_method_id", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = true)
    private LocalDate payPeriodStart;

    @Column(nullable = true)
    private LocalDate payPeriodEnd;

    @Column(nullable = true)
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
    @Column(nullable = false, updatable = false)
    private LocalDateTime processedAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        if (this.processedAt == null) {
            this.processedAt = LocalDateTime.now();
        }
    }
}