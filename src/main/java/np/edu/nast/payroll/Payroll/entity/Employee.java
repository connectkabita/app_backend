package np.edu.nast.payroll.Payroll.entity;

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
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer empId;

    /* =========================
       FK: User (NOT NULL)
       ========================= */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false,unique = true)
    private String email;
    @Column(nullable = false)
    private String contact;
    @Column(nullable = false)
    private String maritalStatus;

    /* =========================
       FK: Designation (NOT NULL)
       ========================= */
    @ManyToOne(optional = false)
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

    /* =========================
       FK: Department (NOT NULL)
       ========================= */
    @ManyToOne(optional = false)
    @JoinColumn(name = "dept_id", nullable = false)
    private Department department;
    @Column(nullable = false)
    private Boolean isActive;

    @Column(updatable = false , nullable = false)
    private LocalDateTime createdAt;

    /* =========================
       Auto timestamp
       ========================= */
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.isActive == null) {
            this.isActive = true;
        }
    }
}
