package np.edu.nast.payroll.Payroll.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payroll_audit")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PayrollAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long auditId;

    @ManyToOne
    @JoinColumn(name = "payroll_id" ,nullable = false)
    private Payroll payroll;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "changed_by",  nullable = false)
    private User changedBy;
    @Column(nullable = false)
    private String changeType;
    @Column(nullable = false)
    @Lob
    private String changeDetails;
    @Column(nullable = false)
    private LocalDateTime changedAt;
}
