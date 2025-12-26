package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.LeaveType;
import java.util.List;

public interface LeaveTypeService {
    LeaveType createLeaveType(LeaveType leaveType);
    List<LeaveType> getAllLeaveTypes();
    LeaveType getLeaveTypeById(Integer id);
    // CHANGE: Use Integer to match Repository
    LeaveType updateLeaveType(Integer id, LeaveType leaveType);
    void deleteLeaveType(Integer id);
}