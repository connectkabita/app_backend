package np.edu.nast.payroll.Payroll.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer empId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String firstName;
    private String lastName;
    private String email;
    private String contact;
    private String maritalStatus;

    @ManyToOne
    @JoinColumn(name = "position_id")
    private Designation position;

    private String education;
    private String employmentStatus;
    private LocalDate joiningDate;
    private String address;

    @ManyToOne
    @JoinColumn(name = "dept_id")
    private Department department;

    private Boolean isActive;
    private LocalDateTime createdAt;
}
