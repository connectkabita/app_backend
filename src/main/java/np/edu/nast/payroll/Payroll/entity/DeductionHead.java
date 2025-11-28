package np.edu.nast.payroll.Payroll.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "deduction_head")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DeductionHead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer deductionHeadId;

    private String name;
    private Double defaultRate;
    private Boolean isPercentage;
    private Boolean statutory;
    private String description;
}
