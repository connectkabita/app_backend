package np.edu.nast.payroll.Payroll.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer empId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String contact;

    @Column(nullable = false)
    private String maritalStatus;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "position_id", nullable = false)
    private Designation position;

    @Column(nullable = false)
    private String education;

    @Column(nullable = false)
    private String employmentStatus;

    @Column(nullable = false)
    private LocalDate joiningDate;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Double basicSalary; // Add this

    @Column(nullable = false)
    private Double allowances; // Add this

    @Column(nullable = false)
    private Double deductions; // Add this

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "dept_id", nullable = false)
    private Department department;

    @Column(nullable = false)
    private Boolean isActive;

    @OneToOne(
            mappedBy = "employee",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonIgnore
    private User user;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.isActive == null) {
            this.isActive = true;
        }
    }
}
