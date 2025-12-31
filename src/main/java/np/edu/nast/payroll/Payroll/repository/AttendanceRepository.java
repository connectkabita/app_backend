package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
    // Fixes DashboardController
    long countByAttendanceDate(LocalDate date);
    List<Attendance> findAllByAttendanceDate(LocalDate date);

    // Fixes AttendanceServiceImpl
    List<Attendance> findByEmployee_EmpId(Integer empId);
}