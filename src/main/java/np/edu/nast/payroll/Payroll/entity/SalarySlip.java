package np.edu.nast.payroll.Payroll.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "salary_slip")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SalarySlip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer slipId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payroll_id", nullable = false)
    private Payroll payroll;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "generated_by",  nullable = false)
    private User generatedBy;
    @Column(nullable = false, updatable = false)
    private LocalDateTime generatedDate;
    @Column(nullable = false)
    private String pdfPath;
}
