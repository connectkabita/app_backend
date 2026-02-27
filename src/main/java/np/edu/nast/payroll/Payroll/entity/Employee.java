package np.edu.nast.payroll.Payroll.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @Column(name = "emp_id")
    @JsonProperty("empId")
    private Integer empId;

    /**
     * FIX: Alias for setId to resolve the error in Attendance.java
     * where you call this.employee.setId(empId)
     */
    public void setId(Integer id) {
        this.empId = id;
    }

    /**
     * FIX: Alias for getId to maintain compatibility with standard naming
     */
    public Integer getId() {
        return empId;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @JsonIgnore
    private User user;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @JsonIgnore
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BankAccount> bankAccounts;

    @JsonIgnore
    public BankAccount getPrimaryBankAccount() {
        if (bankAccounts == null || bankAccounts.isEmpty()) return null;
        return bankAccounts.stream()
                .filter(ba -> Boolean.TRUE.equals(ba.getIsPrimary()))
                .findFirst()
                .orElse(bankAccounts.get(0));
    }

    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 15, nullable = false)
    @Size(min = 10, max = 15, message = "Contact number length must be between 10 and 15")
    private String contact;

    @Column(name = "marital_status", nullable = false)
    private String maritalStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "position_id", nullable = false)
    @JsonIgnoreProperties("employees")
    private Designation position;

    @Column(nullable = false)
    private String education;

    @Column(name = "employment_status", nullable = false)
    private String employmentStatus;

    @Column(name = "joining_date", nullable = false)
    private LocalDate joiningDate;

    @Column(nullable = false)
    private String address;

    @Column(name = "basic_salary", nullable = false)
    private Double basicSalary;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    @JsonIgnoreProperties("employees")
    private Department department;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pay_group_id")
    private PayGroup payGroup;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.isActive == null) this.isActive = true;
        if (this.maritalStatus == null) this.maritalStatus = "SINGLE";
        if (this.employmentStatus == null) this.employmentStatus = "FULL_TIME";
        if (this.basicSalary == null) this.basicSalary = 0.0;
    }
}