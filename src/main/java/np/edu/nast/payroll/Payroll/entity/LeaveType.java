package np.edu.nast.payroll.Payroll.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leave_type")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LeaveType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer leaveTypeId;
    @Column(nullable = false)
    private String typeName;
    @Column(nullable = false)
    private Integer defaultDaysPerYear;
    @Column(nullable = false)
    private Boolean paid;

    // FIX: Added the return statement for the manually defined isPaid() method.
    public Boolean isPaid() {
        return this.paid;
    }
}