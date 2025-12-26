package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.LeaveType;
import np.edu.nast.payroll.Payroll.repository.LeaveTypeRepository;
import np.edu.nast.payroll.Payroll.service.LeaveTypeService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LeaveTypeServiceImpl implements LeaveTypeService {

    private final LeaveTypeRepository leaveTypeRepository;

    public LeaveTypeServiceImpl(LeaveTypeRepository leaveTypeRepository) {
        this.leaveTypeRepository = leaveTypeRepository;
    }

    @Override
    public LeaveType updateLeaveType(Integer id, LeaveType leaveType) {
        // Now id is Integer, matching leaveTypeRepository.findById(Integer)
        LeaveType existing = leaveTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("LeaveType not found"));

        existing.setTypeName(leaveType.getTypeName());
        existing.setDefaultDaysPerYear(leaveType.getDefaultDaysPerYear());
        existing.setPaid(leaveType.isPaid());

        return leaveTypeRepository.save(existing);
    }

    @Override
    public void deleteLeaveType(Integer id) {
        leaveTypeRepository.deleteById(id);
    }

    // Ensure all other override methods also use Integer
    @Override public LeaveType createLeaveType(LeaveType lt) { return leaveTypeRepository.save(lt); }
    @Override public List<LeaveType> getAllLeaveTypes() { return leaveTypeRepository.findAll(); }
    @Override public LeaveType getLeaveTypeById(Integer id) { return leaveTypeRepository.findById(id).orElse(null); }
}