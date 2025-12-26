package np.edu.nast.payroll.Payroll.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payroll_audit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayrollAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id", nullable = false)
    private Long auditId; // Changed to Long wrapper

    @ManyToOne(fetch = FetchType.LAZY) // Lazy is better for performance in audit logs
    @JoinColumn(name = "payroll_id", nullable = false)
    private Payroll payroll; // Correctly maps to the Integer ID in Payroll

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "changed_by", nullable = false)
    private User changedBy;

    @Column(nullable = false, length = 255)
    private String changeType;

    @Column(nullable = false, columnDefinition = "TINYTEXT")
    private String changeDetails;

    @Column(nullable = false, updatable = false)
    private LocalDateTime changedAt;

    @PrePersist
    protected void onCreate() {
        this.changedAt = LocalDateTime.now();
    }
}