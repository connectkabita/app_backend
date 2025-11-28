package np.edu.nast.payroll.Payroll.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer attendanceId;

    @ManyToOne
    @JoinColumn(name = "emp_id")
    private Employee employee;

    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private Double inGpsLat;
    private Double inGpsLong;
    private LocalDate attendanceDate;
}
