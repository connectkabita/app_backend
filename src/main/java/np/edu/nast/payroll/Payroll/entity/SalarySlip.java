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

    @ManyToOne
    @JoinColumn(name = "payroll_id")
    private Payroll payroll;

    @ManyToOne
    @JoinColumn(name = "generated_by")
    private User generatedBy;

    private LocalDateTime generatedDate;
    private String pdfPath;
}
