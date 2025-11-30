package com.todoapp.service;

import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.url}")
    private String appUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendPasswordResetEmail(String email, String resetToken) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("Password Reset Request");
            helper.setText("To reset your password, click the link below:\n\n" +
                    appUrl + "/reset-password?token=" + resetToken + "\n\n" +
                    "This link will expire in 24 hours.\n\n" +
                    "If you did not request this, please ignore this email.", false);
            mailSender.send(mimeMessage);
            System.out.println("Email sent successfully to: " + email);
        } catch (MailAuthenticationException e) {
            Throwable cause = e.getCause();
            if (cause instanceof AuthenticationFailedException) {
                System.err.println("==========================================");
                System.err.println("GMAIL AUTHENTICATION FAILED");
                System.err.println("==========================================");
                System.err.println("Error: " + cause.getMessage());
                System.err.println("\nTo fix this issue:");
                System.err.println("1. Go to your Google Account: https://myaccount.google.com/");
                System.err.println("2. Enable 2-Step Verification (Security > 2-Step Verification)");
                System.err.println("3. Generate an App Password:");
                System.err.println("   - Go to: https://myaccount.google.com/apppasswords");
                System.err.println("   - Select 'Mail' and 'Other (Custom name)'");
                System.err.println("   - Enter 'Todo App' as the name");
                System.err.println("   - Copy the 16-character password (no spaces)");
                System.err.println("4. Update application.properties with the new App Password");
                System.err.println("5. Make sure spring.mail.username is your full Gmail address");
                System.err.println("==========================================");
            } else {
                System.err.println("Email Authentication failed: " + e.getMessage());
                System.err.println("Please verify your email credentials in application.properties");
            }
            e.printStackTrace();
            throw new RuntimeException("Failed to authenticate with email server. Please check your email credentials.", e);
        } catch (MailSendException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send email. Please check your email configuration.", e);
        } catch (Exception e) {
            System.err.println("Unexpected error sending email: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
}


