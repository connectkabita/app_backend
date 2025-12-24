package np.edu.nast.payroll.Payroll.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "deduction_head")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DeductionHead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer deductionHeadId;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Double defaultRate;
    @Column(nullable = false)
    private Boolean isPercentage;
    @Column(nullable = false)
    private Boolean statutory;
    @Column(nullable = false)
    private String description;
}
