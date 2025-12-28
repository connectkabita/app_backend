package np.edu.nast.payroll.Payroll.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "salary_component")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long componentId;

    private String componentName;

    // Foreign key to SalaryComponentType, cannot be null
    @ManyToOne(optional = false)
    @JoinColumn(name = "component_type_id", nullable = false)
    private SalaryComponentType componentType;

    @Column(nullable = false)
    private String calculationMethod; // fixed, percentage_of_basic, formula

    @Column(nullable = false)
    private Double defaultValue;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Boolean required;

    // Helper method to resolve the ID error in your Dashboard Controller
    public Long getComponentTypeId() {
        return (this.componentType != null) ? this.componentType.getComponentTypeId() : null;
    }

    public Boolean isRequired() {
        return this.required;
    }
}