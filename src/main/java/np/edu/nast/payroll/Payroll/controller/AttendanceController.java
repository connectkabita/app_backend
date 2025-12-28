package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.Attendance;
import np.edu.nast.payroll.Payroll.service.AttendanceService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "http://localhost:5173") // Matches your frontend port
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping
    public Attendance create(@RequestBody Attendance attendance) {
        return attendanceService.createAttendance(attendance);
    }

    @PutMapping("/{id}")
    public Attendance update(@PathVariable Long id, @RequestBody Attendance attendance) {
        // Ensuring ID conversion to match your service's expected type
        return attendanceService.updateAttendance(id.intValue(), attendance);
    }

    @GetMapping("/employee/{empId}")
    public List<Attendance> getByEmployee(@PathVariable Integer empId) {
        return attendanceService.getAttendanceByEmployee(empId);
    }
}