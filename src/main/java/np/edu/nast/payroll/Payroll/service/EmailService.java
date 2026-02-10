package np.edu.nast.payroll.Payroll.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import np.edu.nast.payroll.Payroll.entity.Payroll;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Fixes the error in UserServiceImpl
    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset OTP - NAST Payroll");
        message.setText("Your verification code is: " + otp);
        mailSender.send(message);
    }

    // Fixes the error in PayrollController & EsewaController
    public void generateAndSendPayslip(Payroll payroll, String esewaTxnId) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Header Section
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("NAST PAYROLL - SALARY SLIP", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Employee: " + payroll.getEmployee().getFirstName() + " " + payroll.getEmployee().getLastName()));
            document.add(new Paragraph("Pay Date: " + payroll.getPayDate()));
            document.add(new Paragraph("eSewa Ref ID: " + (esewaTxnId != null ? esewaTxnId : "Manual Disbursement")));
            document.add(new Paragraph(" "));

            // Salary Table
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);

            // EARNINGS (Using your Entity fields)
            addTableRow(table, "Basic Salary", payroll.getBasicSalary());
            addTableRow(table, "Total Allowances", payroll.getTotalAllowances());
            addTableRow(table, "Festival Bonus", payroll.getFestivalBonus());
            addTableRow(table, "Other Bonuses", payroll.getOtherBonuses());
            addTotalRow(table, "TOTAL GROSS SALARY", payroll.getGrossSalary());

            // DEDUCTIONS (Using your Entity fields)
            addTableRow(table, "SSF Contribution (11%)", -payroll.getSsfContribution());
            addTableRow(table, "CIT Contribution", -payroll.getCitContribution());
            addTableRow(table, "Income Tax (Monthly TDS)", -payroll.getTotalTax());

            addTotalRow(table, "NET PAYABLE TO BANK", payroll.getNetSalary());

            document.add(table);
            document.close();

            sendEmailWithAttachment(payroll, outputStream.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Error during payslip generation: " + e.getMessage());
        }
    }

    private void sendEmailWithAttachment(Payroll payroll, byte[] pdfBytes) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(payroll.getEmployee().getEmail());
        helper.setSubject("Salary Disbursement Confirmation - " + payroll.getPayDate());
        helper.setText("Dear " + payroll.getEmployee().getFirstName() + ",\n\n" +
                "Your salary for " + payroll.getPayDate() + " has been successfully disbursed. " +
                "Please find the attached PDF payslip for your records.");

        helper.addAttachment("Payslip_" + payroll.getPayDate() + ".pdf", new ByteArrayResource(pdfBytes));
        mailSender.send(message);
    }

    private void addTableRow(PdfPTable table, String label, Double value) {
        table.addCell(new Phrase(label));
        table.addCell(new Phrase("Rs. " + String.format("%.2f", value != null ? value : 0.0)));
    }

    private void addTotalRow(PdfPTable table, String label, Double value) {
        Font bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        PdfPCell c1 = new PdfPCell(new Phrase(label, bold));
        PdfPCell c2 = new PdfPCell(new Phrase("Rs. " + String.format("%.2f", value != null ? value : 0.0), bold));
        table.addCell(c1);
        table.addCell(c2);
    }
}