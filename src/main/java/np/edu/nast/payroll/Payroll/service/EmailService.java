package np.edu.nast.payroll.Payroll.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends OTP for password resets
     */
    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset OTP - NAST Payroll");
        message.setText("Your verification code is: " + otp);
        mailSender.send(message);
    }

    /**
     * NEW: Sends the frontend-generated password to the new employee
     */
    public void sendRegistrationEmail(String to, String fullName, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Welcome to NAST - Your System Credentials");
        message.setText("Dear " + fullName + ",\n\n" +
                "Your employee account has been created successfully.\n\n" +
                "Login Details:\n" +
                "Email: " + to + "\n" +
                "Temporary Password: " + password + "\n\n" +
                "Please login to the portal and change your password immediately for security.\n\n" +
                "Regards,\n" +
                "HR Department, NAST");

        mailSender.send(message);
    }
}