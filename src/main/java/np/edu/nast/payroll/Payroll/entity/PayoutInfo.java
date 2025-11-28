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
    private Integer payoutId;

    @ManyToOne
    @JoinColumn(name = "payroll_id")
    private Payroll payroll;

    @ManyToOne
    @JoinColumn(name = "emp_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "monthly_info_id")
    private MonthlyInfo monthlyInfo;

    private LocalDate paymentDate;

    @ManyToOne
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod paymentMethod;

    @ManyToOne
    @JoinColumn(name = "bank_account_id")
    private BankAccount bankAccount;

    private Double amount;
    private String paymentStatus;
    private String transactionReference;
    private LocalDateTime createdAt;
}
