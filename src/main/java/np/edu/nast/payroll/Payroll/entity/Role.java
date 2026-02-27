package np.edu.nast.payroll.Payroll.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "role_name", nullable = false, unique = true)
    private String roleName;

    // --- NEW ADDITION START ---
    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    @JsonIgnore // Crucial: Prevents infinite JSON loop (Role -> User -> Role...)
    private List<User> users;
    // --- NEW ADDITION END ---

    @PrePersist
    @PreUpdate
    public void normalizeRoleName() {
        if (this.roleName != null) {
            this.roleName = this.roleName.toUpperCase().trim();
        }
    }
}