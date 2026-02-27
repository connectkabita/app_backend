package np.edu.nast.payroll.Payroll.service.impl;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.dto.auth.LoginRequestDTO;
import np.edu.nast.payroll.Payroll.dto.auth.LoginResponseDTO;
import np.edu.nast.payroll.Payroll.dto.auth.SignupRequestDTO;
import np.edu.nast.payroll.Payroll.entity.Employee;
import np.edu.nast.payroll.Payroll.entity.User;
import np.edu.nast.payroll.Payroll.repository.*;
import np.edu.nast.payroll.Payroll.service.AuthService;
import np.edu.nast.payroll.Payroll.security.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final DesignationRepository designationRepository;
    private final DepartmentRepository departmentRepository;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional
    public void registerUser(SignupRequestDTO request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // Plain text for NoOp
        user.setStatus("ACTIVE");

        Integer roleId = (request.getRole_id() != null) ? request.getRole_id() : 2;
        roleRepository.findById(roleId).ifPresent(user::setRole);
        user = userRepository.save(user);

        Employee employee = new Employee();
        employee.setFirstName(request.getFirst_name());
        employee.setLastName(request.getLast_name());
        employee.setEmail(request.getEmail());
        employee.setContact(request.getContact());
        employee.setAddress(request.getAddress());
        employee.setMaritalStatus(request.getMarital_status());
        employee.setJoiningDate(LocalDate.now());
        employee.setIsActive(true);
        employee.setUser(user);

        designationRepository.findById(request.getPosition_id() != null ? request.getPosition_id() : 1).ifPresent(employee::setPosition);
        departmentRepository.findById(request.getDepartment_id() != null ? request.getDepartment_id() : 1).ifPresent(employee::setDepartment);

        employeeRepository.save(employee);
    }

    @Override
    public LoginResponseDTO authenticateUser(LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Employee employee = employeeRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Employee profile not found"));

        String jwt = jwtUtils.generateToken(user.getUsername(), user.getRole().getRoleName());

        return new LoginResponseDTO(user.getUserId(), employee.getEmpId(), user.getUsername(), user.getEmail(), user.getRole().getRoleName(), jwt);
    }

    @Override
    @Transactional
    public void updatePassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email " + email + " not found."));
        user.setPassword(newPassword);
        userRepository.save(user);
    }
}