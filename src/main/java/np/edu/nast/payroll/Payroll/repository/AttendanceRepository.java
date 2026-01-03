package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
    // ✔ Employee attendance history
    List<Attendance> findByEmployee_EmpId(Integer empId);

    // ✔ Daily dashboard count
    long countByAttendanceDate(LocalDate attendanceDate);

    // ✔ Summary cards (Present / Absent / Leave)
    long countByEmployee_EmpIdAndStatus(Integer empId, String status);







    // ✔ Admin dashboard (today)
    @Query("""
        SELECT a 
        FROM Attendance a 
        WHERE a.attendanceDate = CURRENT_DATE 
        ORDER BY a.checkInTime DESC
    """)
    List<Attendance> findTodaysAttendance();





}