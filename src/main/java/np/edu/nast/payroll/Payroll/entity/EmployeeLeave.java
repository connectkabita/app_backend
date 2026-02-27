package np.edu.nast.payroll.Payroll.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "employee_leave")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeLeave {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "leave_id")
    @JsonProperty("leaveId")
    private Integer leaveId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "emp_id", nullable = false)
    @JsonIgnoreProperties({"bankAccounts", "user", "payrolls", "department", "designation", "employeeDocuments"})
    private Employee employee;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    @Column(name = "start_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Column(name = "total_days", nullable = false)
    @JsonProperty("totalDays")
    private Integer totalDays;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(nullable = false, length = 20)
    private String status; // "Pending", "Approved", "Rejected"

    @CreationTimestamp
    @Column(name = "requested_at", nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime requestedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "approved_by_user_id")
    @JsonIgnoreProperties({"password", "employee", "roles"})
    private User approvedBy;

    @Column(name = "approved_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approvedAt;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @PrePersist
    public void handleBeforeInsert() {
        if (this.startDate != null && this.endDate != null) {
            if (this.startDate.isAfter(this.endDate)) {
                throw new IllegalArgumentException("Start date cannot be after end date.");
            }
            long days = ChronoUnit.DAYS.between(this.startDate, this.endDate) + 1;
            this.totalDays = (int) days;
        } else if (this.totalDays == null) {
            this.totalDays = 0;
        }

        if (this.status == null || this.status.isBlank()) {
            this.status = "Pending";
        }
    }
}