package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.Attendance;
import np.edu.nast.payroll.Payroll.entity.Employee;
import np.edu.nast.payroll.Payroll.exception.ResourceNotFoundException;
import np.edu.nast.payroll.Payroll.repository.AttendanceRepository;
import np.edu.nast.payroll.Payroll.repository.EmployeeRepository;
import np.edu.nast.payroll.Payroll.service.AttendanceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    public AttendanceServiceImpl(AttendanceRepository attendanceRepository,
                                 EmployeeRepository employeeRepository) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Attendance createAttendance(Attendance attendance) {
        // FK null check
        if (attendance.getEmployee() == null || attendance.getEmployee().getEmpId() == null) {
            throw new IllegalArgumentException("Employee ID is required for attendance");
        }

        // Check if employee exists
        Employee employee = employeeRepository.findById(attendance.getEmployee().getEmpId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with ID: " + attendance.getEmployee().getEmpId()));

        attendance.setEmployee(employee);
        return attendanceRepository.save(attendance);
    }

    @Override
    public Attendance updateAttendance(Integer id, Attendance updated) {
        Attendance existing = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found with ID: " + id));

        if (updated.getEmployee() == null || updated.getEmployee().getEmpId() == null) {
            throw new IllegalArgumentException("Employee ID is required for attendance update");
        }

        Employee employee = employeeRepository.findById(updated.getEmployee().getEmpId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with ID: " + updated.getEmployee().getEmpId()));

        existing.setCheckInTime(updated.getCheckInTime());
        existing.setCheckOutTime(updated.getCheckOutTime());
        existing.setAttendanceDate(updated.getAttendanceDate());
        existing.setInGpsLat(updated.getInGpsLat());
        existing.setInGpsLong(updated.getInGpsLong());
        existing.setEmployee(employee);

        return attendanceRepository.save(existing);
    }

    @Override
    public void deleteAttendance(Integer id) {
        if (!attendanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Attendance not found with ID: " + id);
        }
        attendanceRepository.deleteById(id);
    }

    @Override
    public Attendance getAttendanceById(Integer id) {
        return attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found with ID: " + id));
    }

    @Override
    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    @Override
    public List<Attendance> getAttendanceByEmployee(Integer empId) {
        // Validate employee existence
        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + empId));

        return attendanceRepository.findByEmployeeEmpId(empId);
    }
}
