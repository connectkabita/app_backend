package np.edu.nast.payroll.Payroll.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "designation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Designation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "designation_id", nullable = false)
    private Integer designationId;

    @Column(name = "designation_title", nullable = false)
    private String designationTitle;

    @Column(name = "base_salary", nullable = false)
    private double baseSalary;

    @OneToMany(mappedBy = "position", fetch = FetchType.LAZY)
    @JsonIgnore // Prevents infinite recursion in JSON
    private List<Employee> employees;
}