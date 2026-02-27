package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.Attendance;
import np.edu.nast.payroll.Payroll.service.AttendanceService;
import np.edu.nast.payroll.Payroll.service.EmployeeService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "*")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final EmployeeService employeeService;

    public AttendanceController(AttendanceService attendanceService, EmployeeService employeeService) {
        this.attendanceService = attendanceService;
        this.employeeService = employeeService;
    }

    /**
     * FETCH HISTORY FOR LOGGED IN USER
     * Matches Android: @GET("api/attendance/my-history")
     */
    @GetMapping("/my-history")
    public List<Attendance> getMyHistory() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer empId = employeeService.findIdByUsername(username);
        return attendanceService.getAttendanceByEmployee(empId);
    }

    /**
     * CLOCK IN / PUNCH
     * Matches Android: @POST("api/attendance/punch")
     */
    @PostMapping("/punch")
    public Attendance create(@RequestBody Attendance attendance) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer empId = employeeService.findIdByUsername(username);

        // Force the attendance record to belong to the logged-in user
        attendance.setEmpId(empId);

        if (attendance.getCheckInTime() == null) {
            attendance.setCheckInTime(LocalDateTime.now());
        }
        return attendanceService.createAttendance(attendance);
    }

    /**
     * STATS FOR DASHBOARD (Dynamic)
     * Matches Android: @GET("api/attendance/my-stats/{year}/{month}")
     */
    @GetMapping("/my-stats/{year}/{month}")
    public Map<String, Object> getMyStats(@PathVariable int year, @PathVariable int month) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer empId = employeeService.findIdByUsername(username);
        return attendanceService.getMonthlyStats(empId, year, month);
    }

    /**
     * ADMIN/SYSTEM USE ONLY: Update attendance by ID
     */
    @PutMapping("/{id}")
    public Attendance update(@PathVariable Integer id, @RequestBody Attendance attendance) {
        return attendanceService.updateAttendance(id, attendance);
    }

    /**
     * ADMIN/SYSTEM USE ONLY: Get specific employee history
     */
    @GetMapping("/employee/{empId}")
    public List<Attendance> getByEmployee(@PathVariable Integer empId) {
        return attendanceService.getAttendanceByEmployee(empId);
    }
}