package np.edu.nast.payroll.Payroll.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leave_balance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "balance_id", nullable = false)
    private Integer balanceId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "emp_id", nullable = false) // Matches your 'emp_id' column in screenshot
    @JsonIgnore
    private Employee employee;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "leave_type_id", nullable = false) // Matches your 'leave_type_id' column
    private LeaveType leaveType;

    @Column(name = "current_balance_days", nullable = false)
    @JsonProperty("currentBalanceDays") // Matches the 'it.currentBalanceDays' in your Kotlin Fragment
    private Double currentBalanceDays;

    @Column(nullable = false)
    private Integer year;
}