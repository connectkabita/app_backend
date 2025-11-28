package np.edu.nast.payroll.Payroll.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "employee_salary_component")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmployeeSalaryComponent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer escId;

    @ManyToOne
    @JoinColumn(name = "emp_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "component_id")
    private SalaryComponent salaryComponent;

    private Double value;
    private Boolean isActive;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
}
