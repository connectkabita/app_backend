package np.edu.nast.payroll.Payroll.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leave_type")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LeaveType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer leaveTypeId;

    private String typeName;
    private Integer defaultDaysPerYear;
    private Boolean paid;

    // FIX: Added the return statement for the manually defined isPaid() method.
    public Boolean isPaid() {
        return this.paid;
    }
}