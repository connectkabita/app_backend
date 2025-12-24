package np.edu.nast.payroll.Payroll.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "payout_info")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PayoutInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer payoutId;

    @ManyToOne
    @JoinColumn(name = "payroll_id", nullable = false)
    private Payroll payroll;

    @ManyToOne
    @JoinColumn(name = "emp_id",  nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "monthly_info_id",  nullable = false)
    private MonthlyInfo monthlyInfo;
    @Column(nullable = false)
    private LocalDate paymentDate;

    @ManyToOne
    @JoinColumn(name = "payment_method_id",nullable = false)
    private PaymentMethod paymentMethod;

    @ManyToOne
    @JoinColumn(name = "bank_account_id", nullable = false)
    private BankAccount bankAccount;
    @Column(nullable = false)
    private Double amount;
    @Column(nullable = false)
    private String paymentStatus;
    @Column(nullable = false)
    private String transactionReference;
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
