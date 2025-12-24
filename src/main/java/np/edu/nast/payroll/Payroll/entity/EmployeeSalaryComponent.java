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
    @Column(nullable = false)
    private Integer escId;

    @ManyToOne
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "component_id", nullable = false)
    private SalaryComponent salaryComponent;
    @Column(nullable = false)
    private Double value;
    @Column(nullable = false)
    private Boolean isActive;
    @Column(nullable = false)
    private LocalDate effectiveFrom;
    @Column(nullable = false)
    private LocalDate effectiveTo;
}
