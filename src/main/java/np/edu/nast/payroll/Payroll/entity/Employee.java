package np.edu.nast.payroll.Payroll.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
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

    @OneToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "user_id", referencedColumnName = "userId", nullable = true)
    @JsonIgnore // Prevents infinite recursion with the User entity
    private User user;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    // Logic Update: Changed to String to handle 10 digits/leading zeros correctly
    @Column(length = 10, nullable = false)
    @Size(min = 10, max = 10, message = "Contact number must be exactly 10 digits")
    private String contact;

    @Column(nullable = false)
    private String maritalStatus;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "position_id", nullable = false)
    @JsonIgnoreProperties("employees")
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

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "dept_id", nullable = false)
    @JsonIgnoreProperties("employees")
    private Department department;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.isActive == null) this.isActive = true;
        if (this.maritalStatus == null) this.maritalStatus = "SINGLE";
        if (this.employmentStatus == null) this.employmentStatus = "FULL_TIME";
        if (this.basicSalary == null) this.basicSalary = 0.0;
    }
}