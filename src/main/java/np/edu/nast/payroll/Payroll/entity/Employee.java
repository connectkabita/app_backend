package np.edu.nast.payroll.Payroll.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

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
    @JsonIgnore // 🔥 CRITICAL FIX
    private User user;

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

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;


    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    private List<BankAccount> bankAccounts;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.isActive == null) this.isActive = true;
        if (this.maritalStatus == null) this.maritalStatus = "SINGLE";
        if (this.employmentStatus == null) this.employmentStatus = "FULL_TIME";
        if (this.basicSalary == null) this.basicSalary = 0.0;
        if (this.allowances == null) this.allowances = 0.0;
        if (this.deductions == null) this.deductions = 0.0;
    }



}
