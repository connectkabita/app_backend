package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

    List<Attendance> findByEmployee_EmpIdAndAttendanceDateBetween(Integer empId, LocalDate start, LocalDate end);

    List<Attendance> findByEmployee_EmpIdAndAttendanceDateGreaterThanEqualAndAttendanceDateLessThan(Integer empId, LocalDate start, LocalDate end);

    long countByAttendanceDate(LocalDate date);

    List<Attendance> findAllByAttendanceDate(LocalDate date);

    List<Attendance> findByEmployee_EmpId(Integer empId);

    Optional<Attendance> findTopByEmployee_EmpIdOrderByAttendanceIdDesc(Integer empId);

    long countByEmployeeEmpIdAndAttendanceDateBetween(Integer empId, LocalDate start, LocalDate end);

    long countByEmployee_EmpIdAndStatusAndAttendanceDateBetween(Integer empId, String status, LocalDate start, LocalDate end);

    // --- UPDATED: Dynamic Filtering for Dashboard ---
    @Query("""
        SELECT a FROM Attendance a 
        WHERE YEAR(a.attendanceDate) = :year 
        AND MONTH(a.attendanceDate) = :month 
        AND DAY(a.attendanceDate) = :day 
        AND (:search IS NULL OR :search = '' OR 
             LOWER(a.employee.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR 
             LOWER(a.employee.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR 
             CAST(a.employee.empId AS string) LIKE CONCAT('%', :search, '%'))
    """)
    List<Attendance> findFilteredAttendance(
            @Param("year") int year,
            @Param("month") int month,
            @Param("day") int day,
            @Param("search") String search
    );

    @Query("""
    SELECT 
        SUM(CASE WHEN a.status='PRESENT' THEN 1 ELSE 0 END),
        SUM(CASE WHEN a.status='ABSENT' THEN 1 ELSE 0 END),
        SUM(CASE WHEN a.status='LEAVE' THEN 1 ELSE 0 END)
    FROM Attendance a 
    WHERE YEAR(a.attendanceDate)=:year AND MONTH(a.attendanceDate)=:month
    """)
    List<Object[]> summary(@Param("year") int year, @Param("month") int month);
}