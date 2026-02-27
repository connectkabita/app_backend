package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.EmployeeLeave;
import java.util.List;

public interface EmployeeLeaveService {

    /**
     * Creates a new leave request.
     * Logic inside the implementation should handle initial status ("Pending")
     * and calculate total days before saving.
     */
    EmployeeLeave requestLeave(EmployeeLeave leave);

    List<EmployeeLeave> getAllLeaves();

    /**
     * NEW: Supports the admin portal filtering logic.
     * Fixes the "Method does not override" error in the Implementation class.
     */
    List<EmployeeLeave> getFilteredLeaves(Integer year, Integer month, String status, String search);

    EmployeeLeave getLeaveById(Integer id);

    /**
     * Used by the /my-history endpoint on Android to filter
     * leaves for the logged-in employee.
     */
    List<EmployeeLeave> getLeavesByEmployee(Integer empId);

    void deleteLeave(Integer id);

    EmployeeLeave updateLeave(Integer id, EmployeeLeave leave);

    /**
     * UPDATED: Processes approval/rejection.
     * @param id The ID of the leave request.
     * @param status The new status ("Approved" or "Rejected").
     * @param adminId The User ID of the administrator making the decision.
     * @param rejectionReason Optional text provided if status is "Rejected".
     */
    EmployeeLeave updateLeaveStatus(Integer id, String status, Integer adminId, String rejectionReason);
}