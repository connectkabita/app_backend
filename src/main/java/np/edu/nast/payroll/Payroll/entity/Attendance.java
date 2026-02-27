package np.edu.nast.payroll.Payroll.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attendanceId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    // Helper field for the Controller/Android app
    @Transient // This won't be saved in the DB, but allows setEmpId() to work
    private Integer empId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime checkInTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime checkOutTime;

    // Renamed to match Android "latitude/longitude" or used for internal logic
    private Double latitude;
    private Double longitude;

    private String type; // CLOCK_IN or CLOCK_OUT
    private String workLocation;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate attendanceDate;

    @Column(nullable = false)
    private String status;

    /**
     * This fixes the "Cannot resolve method setEmpId" error in your Controller.
     */
    public void setEmpId(Integer empId) {
        this.empId = empId;
        if (this.employee == null) {
            this.employee = new Employee();
        }
        this.employee.setId(empId);
    }
}