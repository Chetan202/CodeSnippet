package com.example.CodeHub.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public void sendVerificationEmail(String toEmail, String token) {
        try {
            logger.info("Preparing to send verification email to: {}", toEmail);
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);  // Use the actual email address from properties
            message.setTo(toEmail);
            message.setSubject("CodeHub - Verify Your Email");
            
            String verificationLink = baseUrl + "/verify?token=" + token;
            String emailContent = "Hello,\n" +
                    "\n" +
                    "Thank you for registering with CodeHub. Please click on the link below to verify your email address:\n" +
                    "\n" +
                    verificationLink + "\n" +
                    "\n" +
                    "If you did not request this, please ignore this email.\n" +
                    "\n" +
                    "Best regards,\n" +
                    "The CodeHub Team";
            
            message.setText(emailContent);
            
            logger.info("Email content prepared. Sending to: {}", toEmail);
            logger.debug("Verification link: {}", verificationLink);
            
            mailSender.send(message);
            logger.info("Email successfully sent to: {}", toEmail);
        } catch (MailException e) {
            logger.error("Failed to send email to {}: {}", toEmail, e.getMessage(), e);
            // Log more detailed error information
            logger.error("Exception type: {}", e.getClass().getName());
            if (e.getCause() != null) {
                logger.error("Root cause: {}", e.getCause().getMessage());
            }
        }
    }
}