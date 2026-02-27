package np.edu.nast.payroll.Payroll.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leave_type")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LeaveType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "leave_type_id")
    private Integer leaveTypeId;

    @Column(name = "type_name", nullable = false, unique = true)
    private String typeName;

    @Column(name = "default_days_per_year", nullable = false)
    private Integer defaultDaysPerYear;

    @Column(nullable = false)
    private Boolean paid;

    /**
     * Explicit helper for Boolean logic.
     * Useful for checking "if (leaveType.isPaid()) { ... }" in Payroll calculations.
     */
    public Boolean isPaid() {
        return this.paid != null && this.paid;
    }
}