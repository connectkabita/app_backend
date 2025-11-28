package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.Attendance;
import np.edu.nast.payroll.Payroll.repository.AttendanceRepository;
import np.edu.nast.payroll.Payroll.service.AttendanceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;

    public AttendanceServiceImpl(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    public Attendance createAttendance(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    @Override
    public Attendance updateAttendance(Long id, Attendance attendance) {
        Attendance existing = attendanceRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new RuntimeException("Attendance not found"));

        existing.setCheckInTime(attendance.getCheckInTime());
        existing.setCheckOutTime(attendance.getCheckOutTime());
        existing.setAttendanceDate(attendance.getAttendanceDate());
        existing.setInGpsLat(attendance.getInGpsLat());
        existing.setInGpsLong(attendance.getInGpsLong());

        return attendanceRepository.save(existing);
    }

    @Override
    public void deleteAttendance(Long id) {
        attendanceRepository.deleteById(Math.toIntExact(id));
    }

    @Override
    public Attendance getAttendanceById(Long id) {
        return attendanceRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new RuntimeException("Attendance not found"));
    }

    @Override
    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    @Override
    public List<Attendance> getAttendanceByEmployee(Long empId) {
        return attendanceRepository.findByEmployeeEmpId(empId);
    }
}
