package np.edu.nast.payroll.Payroll.controller;

import lombok.RequiredArgsConstructor;
import np.edu.nast.payroll.Payroll.entity.User;
import np.edu.nast.payroll.Payroll.repository.UserRepository;
import np.edu.nast.payroll.Payroll.salaryDTO.EmployeeSalaryAnalyticsDTO;
import np.edu.nast.payroll.Payroll.service.SalaryAnalyticsService;
import np.edu.nast.payroll.Payroll.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/salary-analytics")
@RequiredArgsConstructor
public class SalaryAnalyticsController {

    private final SalaryAnalyticsService salaryAnalyticsService;


    @GetMapping("/me")
    public ResponseEntity<?> getMySalary(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String month
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        YearMonth yearMonth;
        try {
            yearMonth = YearMonth.parse(month);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid month format. Use yyyy-MM");
        }

        return ResponseEntity.ok(
                salaryAnalyticsService.getSalaryForUser(
                        userDetails.getUsername(),
                        yearMonth
                )
        );
    }



}
