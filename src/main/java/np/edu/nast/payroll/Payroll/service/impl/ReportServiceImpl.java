package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.dto.auth.SalaryReportDTO;
import np.edu.nast.payroll.Payroll.entity.Employee;
import np.edu.nast.payroll.Payroll.entity.Report;
import np.edu.nast.payroll.Payroll.repository.EmployeeRepository;
import np.edu.nast.payroll.Payroll.repository.ReportRepository;
import np.edu.nast.payroll.Payroll.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public List<Report> getAllReports() {
        return reportRepository.findAllByOrderByDateGeneratedDesc();
    }

    @Override
    public void generateAndSaveReport(String category) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String fileName = category.replace(" ", "_") + "_" + timestamp + ".csv";

        Report report = Report.builder()
                .fileName(fileName)
                .category(category.toUpperCase())
                .dateGenerated(LocalDateTime.now())
                .filePath(category.toUpperCase())
                .fileSize("15 KB")
                .build();

        reportRepository.save(report);
    }

    @Override
    public Report getReportById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with ID: " + id));
    }

    @Override
    public byte[] getFileData(String categoryIdentifier) {
        List<Employee> employees = employeeRepository.findAll();
        StringBuilder csv = new StringBuilder();
        String cat = (categoryIdentifier != null) ? categoryIdentifier.toUpperCase() : "GENERAL";

        // Date formatter to fix the ####### issue in Excel
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 1. SALARY SUMMARIES
        if (cat.contains("SALARY")) {
            csv.append("Full Name,Department,Basic Salary,Allowance,Deduction,Net Pay\n");
            for (Employee emp : employees) {
                double basic = (emp.getBasicSalary() != null ? emp.getBasicSalary() : 0);
                double allow = (emp.getAllowances() != null ? emp.getAllowances() : 0);
                double deduct = (emp.getDeductions() != null ? emp.getDeductions() : 0);
                double net = basic + allow - deduct;

                csv.append(emp.getFirstName()).append(" ").append(emp.getLastName()).append(",")
                        .append(emp.getDepartment() != null ? emp.getDepartment().getDeptName() : "N/A").append(",")
                        .append(basic).append(",")
                        .append(allow).append(",")
                        .append(deduct).append(",")
                        .append(net).append("\n");
            }
        }
        // 2. TAX & SSF REPORTS
        else if (cat.contains("TAX") || cat.contains("SSF")) {
            csv.append("Employee ID,Full Name,Email,Basic Salary,Total Deductions (Tax/SSF)\n");
            for (Employee emp : employees) {
                csv.append(emp.getEmpId()).append(",")
                        .append(emp.getFirstName()).append(" ").append(emp.getLastName()).append(",")
                        .append(emp.getEmail()).append(",")
                        .append(emp.getBasicSalary() != null ? emp.getBasicSalary() : 0).append(",")
                        .append(emp.getDeductions() != null ? emp.getDeductions() : 0).append("\n");
            }
        }
        // 3. ATTENDANCE LOGS
        else if (cat.contains("ATTENDANCE")) {
            csv.append("Employee ID,Full Name,Active Stat,Joining Date\n");
            for (Employee emp : employees) {
                // Formatting date to string prevents Excel from showing #######
                String formattedDate = (emp.getJoiningDate() != null) ? emp.getJoiningDate().format(dateFormatter) : "N/A";

                csv.append(emp.getEmpId()).append(",")
                        .append(emp.getFirstName()).append(" ").append(emp.getLastName()).append(",")
                        .append(emp.getIsActive() != null && emp.getIsActive() ? "Active" : "Inactive").append(",")
                        .append(formattedDate).append("\n");
            }
        }
        // 4. EMPLOYEE HISTORY
        else {
            csv.append("ID,Full Name,Email,Contact,Position,Education\n");
            for (Employee emp : employees) {
                csv.append(emp.getEmpId()).append(",")
                        .append(emp.getFirstName()).append(" ").append(emp.getLastName()).append(",")
                        .append(emp.getEmail()).append(",")
                        .append(emp.getContact()).append(",")
                        .append(emp.getPosition() != null ? emp.getPosition().getDesignationTitle() : "N/A").append(",")
                        .append(emp.getEducation()).append("\n");
            }
        }

        return csv.toString().getBytes();
    }

    @Override
    public List<SalaryReportDTO> getSalarySummaryData() {
        return employeeRepository.findAll().stream()
                .map(emp -> SalaryReportDTO.builder()
                        .employeeName(emp.getFirstName() + " " + emp.getLastName())
                        .designation(emp.getPosition() != null ? emp.getPosition().getDesignationTitle() : "N/A")
                        .basicSalary(emp.getBasicSalary())
                        .totalAllowances(emp.getAllowances())
                        .totalDeductions(emp.getDeductions())
                        .netPay((emp.getBasicSalary() != null ? emp.getBasicSalary() : 0) +
                                (emp.getAllowances() != null ? emp.getAllowances() : 0) -
                                (emp.getDeductions() != null ? emp.getDeductions() : 0))
                        .paymentStatus(emp.getIsActive() != null && emp.getIsActive() ? "Active" : "Inactive")
                        .build())
                .collect(Collectors.toList());
    }
}