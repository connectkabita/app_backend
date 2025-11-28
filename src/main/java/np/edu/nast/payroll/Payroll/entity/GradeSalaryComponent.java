package np.edu.nast.payroll.Payroll.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "grade_salary_component")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GradeSalaryComponent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer gscId;

    @ManyToOne
    @JoinColumn(name = "grade_id")
    private SalaryGrade grade;

    @ManyToOne
    @JoinColumn(name = "component_id")
    private SalaryComponent component;

    private Double value;
}
