package np.edu.nast.payroll.Payroll.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "salary_grade")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SalaryGrade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gradeId;
    @Column(nullable = false)
    private String gradeName;
    @Column(nullable = false)
    private String description;
}
