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
    private Integer auditId;

    @ManyToOne
    @JoinColumn(name = "payroll_id")
    private Payroll payroll;

    @ManyToOne
    @JoinColumn(name = "changed_by")
    private User changedBy;

    private String changeType;

    @Lob
    private String changeDetails;

    private LocalDateTime changedAt;
}
