package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.Attendance;
import np.edu.nast.payroll.Payroll.repository.EmployeeRepository;
import np.edu.nast.payroll.Payroll.repository.EmployeeLeaveRepository;
import np.edu.nast.payroll.Payroll.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:5173")
public class DashboardController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeLeaveRepository leaveRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @GetMapping("/stats")
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // 1. Total Workforce Count
        long totalEmployees = employeeRepository.count();

        // 2. Pending Leaves (Matches the custom method in Repository)
        long pendingLeaves = leaveRepository.countByStatus("Pending");

        // 3. Attendance Calculation
        long presentToday = attendanceRepository.countByAttendanceDate(LocalDate.now());

        String attendancePercentage = totalEmployees > 0
                ? (presentToday * 100 / totalEmployees) + "%"
                : "0%";

        stats.put("totalWorkforce", totalEmployees);
        stats.put("leaveRequests", pendingLeaves);
        stats.put("dailyAttendance", attendancePercentage);

        return stats;
    }

    @GetMapping("/recent-attendance")
    public List<Attendance> getRecentAttendance() {
        // Fetches list of today's check-ins for the dashboard table
        return attendanceRepository.findAllByAttendanceDate(LocalDate.now());
    }
}