package np.edu.nast.payroll.Payroll.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "salary_component")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SalaryComponent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer componentId;

    private String componentName;

    @ManyToOne
    @JoinColumn(name = "component_type_id")
    private SalaryComponentType componentType;

    private String calculationMethod; // fixed, percentage_of_basic, formula
    private Double defaultValue;
    private String description;
    private Boolean required;

    public Boolean isRequired() {
        return this.required;

    }
}
