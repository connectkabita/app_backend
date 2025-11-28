package np.edu.nast.payroll.Payroll.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "salary_component_type")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SalaryComponentType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer componentTypeId;

    private String name;
    private String description;
}
