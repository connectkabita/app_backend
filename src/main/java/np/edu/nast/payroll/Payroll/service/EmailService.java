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

    /**
     * Sends a simple text OTP for password resets.
     */
    public void sendOtpEmail(String to, String otp) {
        if (to == null || to.isEmpty()) {
            throw new RuntimeException("Recipient email address is missing.");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("kabita.dhakal.tech@gmail.com");
        message.setTo(to);
        message.setSubject("Password Reset OTP - NAST Payroll");
        message.setText("Your verification code is: " + otp + "\n\nThis code will expire in 10 minutes.");

        mailSender.send(message);
    }

    /**
     * Generates a PDF Payslip and sends it as an attachment.
     */
    public void generateAndSendPayslip(Payroll payroll, String esewaTxnId) {
        if (payroll == null || payroll.getEmployee() == null) {
            throw new RuntimeException("Cannot generate payslip: Payroll or Employee data is null.");
        }

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // PDF Content
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("NAST Payroll - SALARY SLIP", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Employee: " + payroll.getEmployee().getFirstName() + " " + payroll.getEmployee().getLastName()));
            document.add(new Paragraph("Pay Date: " + payroll.getPayDate()));
            document.add(new Paragraph("Payment Method: " + (esewaTxnId != null ? "eSewa (Ref: " + esewaTxnId + ")" : "Bank Transfer")));
            document.add(new Paragraph(" "));

            // Salary Table
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);

            addTableRow(table, "Basic Salary", payroll.getBasicSalary());
            addTableRow(table, "Total Allowances", payroll.getTotalAllowances());
            addTableRow(table, "Bonus", (payroll.getFestivalBonus() != null ? payroll.getFestivalBonus() : 0.0) + (payroll.getOtherBonuses() != null ? payroll.getOtherBonuses() : 0.0));
            addTableRow(table, "Deductions (Tax/SSF)", -(payroll.getTotalTax() + (payroll.getSsfContribution() != null ? payroll.getSsfContribution() : 0.0)));
            addTotalRow(table, "NET PAYABLE", payroll.getNetSalary());

            document.add(table);
            document.close();

            sendEmailWithAttachment(payroll, outputStream.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error during payslip generation/emailing: " + e.getMessage());
        }
    }

    private void sendEmailWithAttachment(Payroll payroll, byte[] pdfBytes) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(payroll.getEmployee().getEmail());
        helper.setSubject("Salary Slip - " + payroll.getPayDate());
        helper.setText("Dear " + payroll.getEmployee().getFirstName() + ",\n\nYour salary slip is attached.");

        helper.addAttachment("Payslip_" + payroll.getPayDate() + ".pdf", new ByteArrayResource(pdfBytes));
        mailSender.send(message);
    }

    private void addTableRow(PdfPTable table, String label, Double value) {
        table.addCell(new Phrase(label));
        table.addCell(new Phrase("Rs. " + String.format("%.2f", value != null ? value : 0.0)));
    }

    private void addTotalRow(PdfPTable table, String label, Double value) {
        Font bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        table.addCell(new PdfPCell(new Phrase(label, bold)));
        table.addCell(new PdfPCell(new Phrase("Rs. " + String.format("%.2f", value != null ? value : 0.0), bold)));
    }
}