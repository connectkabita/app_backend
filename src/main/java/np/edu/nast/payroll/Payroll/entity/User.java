package np.edu.nast.payroll.Payroll.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    @JsonIgnoreProperties("users") // Prevents Role -> Users -> Role loop
    private Role role;

    @Builder.Default
    @Column(length = 20)
    private String status = "ACTIVE";

    @Builder.Default
    @Column(nullable = false)
    private boolean enabled = true;

    /**
     * Link to the Employee record.
     * mappedBy refers to the "user" field in the Employee class.
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("user") // Prevents User -> Employee -> User loop
    private Employee employee;

    @Column(name = "reset_token")
    @JsonIgnore
    private String resetToken;

    @Column(name = "token_expiry")
    @JsonIgnore
    private LocalDateTime tokenExpiry;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "ACTIVE";
        }
    }

    // Helper method to ensure Spring Security always sees the boolean status correctly
    public boolean isEnabled() {
        return this.enabled;
    }
}