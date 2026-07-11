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
    
    @Value("${app.mail-from:${spring.mail.username}}")
    private String fromEmail;
    
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${app.mail-retry-attempts:3}")
    private int retryAttempts;

    public boolean sendVerificationEmail(String toEmail, String token) {
        SimpleMailMessage message = buildVerificationMessage(toEmail, token);
        for (int attempt = 1; attempt <= retryAttempts; attempt++) {
            try {
                logger.info("Sending verification email to {}. Attempt {}/{}", toEmail, attempt, retryAttempts);
                mailSender.send(message);
                logger.info("Email successfully sent to: {}", toEmail);
                return true;
            } catch (MailException e) {
                logger.warn("Verification email attempt {}/{} failed for {}: {}",
                        attempt, retryAttempts, toEmail, e.getMessage());
                if (attempt == retryAttempts) {
                    logger.error("Failed to send email to {} after {} attempts", toEmail, retryAttempts, e);
                    if (e.getCause() != null) {
                        logger.error("Root cause: {}", e.getCause().getMessage());
                    }
                }
            }
        }
        return false;
    }

    private SimpleMailMessage buildVerificationMessage(String toEmail, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
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
        logger.debug("Verification link: {}", verificationLink);
        return message;
    }
}
