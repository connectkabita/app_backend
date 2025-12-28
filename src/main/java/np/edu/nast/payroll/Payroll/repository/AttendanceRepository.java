package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
    List<Attendance> findByEmployeeEmpId(Integer empId);
    long countByAttendanceDate(LocalDate attendanceDate);

    // ADD THIS: Fetches only today's attendance records ordered by latest check-in
    @Query("SELECT a FROM Attendance a WHERE a.attendanceDate = CURRENT_DATE ORDER BY a.checkInTime DESC")
    List<Attendance> findTodaysAttendance();
}