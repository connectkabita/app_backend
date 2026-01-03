package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;



@Repository
public interface AttendanceReportRepository extends JpaRepository<Attendance, Long> {

    @Query("""
        SELECT COUNT(a)
        FROM Attendance a
        WHERE a.status = 'PRESENT'
          AND MONTH(a.attendanceDate) = :month
          AND YEAR(a.attendanceDate) = :year
    """)
    long countPresent(int month, int year);

    @Query("""
        SELECT COUNT(a)
        FROM Attendance a
        WHERE a.status = 'ABSENT'
          AND MONTH(a.attendanceDate) = :month
          AND YEAR(a.attendanceDate) = :year
    """)
    long countAbsent(int month, int year);

    // Since you DO NOT store late info, derive it logically
    @Query("""
        SELECT COUNT(a)
        FROM Attendance a
        WHERE a.checkInTime IS NOT NULL
          AND FUNCTION('HOUR', a.checkInTime) > 9
          AND MONTH(a.attendanceDate) = :month
          AND YEAR(a.attendanceDate) = :year
    """)
    long countLate(int month, int year);

    // You DO NOT store overtime → return 0 safely
    @Query("""
        SELECT 0.0
        FROM Attendance a
        WHERE MONTH(a.attendanceDate) = :month
          AND YEAR(a.attendanceDate) = :year
    """)
    double sumOvertimeHours(int month, int year);

    @Query("""
        SELECT 0.0
        FROM Attendance a
        WHERE MONTH(a.attendanceDate) = :month
          AND YEAR(a.attendanceDate) = :year
    """)
    double calculateOvertimePay(int month, int year);
}
