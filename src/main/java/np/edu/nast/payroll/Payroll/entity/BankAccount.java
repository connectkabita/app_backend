package np.edu.nast.payroll.Payroll.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bank_account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer accountId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "emp_id", nullable = false)
    @JsonIgnore
    private Employee employee;

    @ManyToOne(optional = false)
    @JoinColumn(name = "bank_id", nullable = false)
    private Bank bank;

    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;

    @Column(nullable = false)
    private String accountType;

    // Changed name to avoid SQL reserved word "PRIMARY"
    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.isPrimary == null) {
            this.isPrimary = false;
        }
    }
}