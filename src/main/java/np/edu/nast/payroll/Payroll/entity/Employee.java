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
    private Double basicSalary;

    @Column(nullable = false)
    private Double allowances;

    @Column(nullable = false)
    private Double deductions;

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

    /**
     * This method runs before the data is saved to the database.
     * It prevents "null property" errors by providing default values.
     */
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();

        // Default values for mandatory fields to prevent persistence errors
        if (this.isActive == null) {
            this.isActive = true;
        }
        if (this.maritalStatus == null) {
            this.maritalStatus = "SINGLE";
        }
        if (this.employmentStatus == null) {
            this.employmentStatus = "FULL_TIME";
        }
        if (this.basicSalary == null) {
            this.basicSalary = 0.0;
        }
        if (this.allowances == null) {
            this.allowances = 0.0;
        }
        if (this.deductions == null) {
            this.deductions = 0.0;
        }
    }
}
