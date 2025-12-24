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

    /* =========================
       Primary Key
       ========================= */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer payrollId;

    /* =========================
       REQUIRED FOREIGN KEYS
       ========================= */

    /**
     * Employee for whom payroll is generated
     * REQUIRED: payroll cannot exist without employee
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    /**
     * User who processed payroll (Admin / Accountant)
     * REQUIRED for audit and accountability
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "processed_by", nullable = false)
    private User processedBy;

    /**
     * Bank account where salary is paid
     * REQUIRED: ensures payment destination exists
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "payment_account_id", nullable = false)
    private BankAccount paymentAccount;

    /* =========================
       OPTIONAL FOREIGN KEYS
       ========================= */

    /**
     * Pay group (Monthly, Weekly, Contract, etc.)
     * Optional by business rule
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pay_group_id", nullable = false)
    private PayGroup payGroup;

    /**
     * Payment method (Bank Transfer, Cash, Cheque, etc.)
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "payment_method_id", nullable = false)
    private PaymentMethod paymentMethod;

    /* =========================
       PAY PERIOD INFORMATION
       ========================= */

    @Column(nullable = false)
    private LocalDate payPeriodStart;

    @Column(nullable = false)
    private LocalDate payPeriodEnd;

    @Column(nullable = false)
    private LocalDate payDate;

    /* =========================
       SALARY BREAKDOWN
       ========================= */

    @Column(nullable = false)
    private Double grossSalary;

    @Column(nullable = false)
    private Double totalAllowances;

    @Column(nullable = false)
    private Double totalDeductions;

    @Column(nullable = false)
    private Double totalTax;

    @Column(nullable = false)
    private Double netSalary;

    /* =========================
       STATUS & AUDIT
       ========================= */

    /**
     * Allowed values (recommended):
     * DRAFT, PROCESSED, PAID, REVERSED
     */
    @Column(nullable = false, length = 20)
    private String status;

    /**
     * Timestamp when payroll was processed
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime processedAt;

    /* =========================
       LIFECYCLE CALLBACKS
       ========================= */

    /**
     * Ensures processedAt is always set
     * even if client forgets to send it
     */
    @PrePersist
    protected void onCreate() {
        if (this.processedAt == null) {
            this.processedAt = LocalDateTime.now();
        }
    }
}
