package com.example.CodeHub.Services;

import com.example.CodeHub.Dto.UserDto;
import com.example.CodeHub.Entity.User;
import com.example.CodeHub.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final String adminEmail;
    private final String mailFrom;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JavaMailSender mailSender,
                           @Value("${app.admin-email:chetanjha888@gmail.com}") String adminEmail,
                           @Value("${app.mail-from:}") String mailFrom) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.adminEmail = adminEmail;
        this.mailFrom = mailFrom;
    }

    @Override
    public User registerUser(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()) != null) {
            throw new IllegalArgumentException("Email already registered");
        }
        if (userRepository.findByUsername(userDto.getUsername()) != null) {
            throw new IllegalArgumentException("Username already taken");
        }
        User user = new User(userDto.getUsername(),
                passwordEncoder.encode(userDto.getPassword()),
                userDto.getEmail());
        user.setRole(adminEmail.equalsIgnoreCase(userDto.getEmail()) ? "ADMIN" : "USER");
        user.setVerified(false);
        String otp = generateOtp();
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);
        sendOtpEmail(userDto.getEmail(), otp);
        return user;
    }

    @Override
    public boolean verifyOtp(String email, String otp) {
        User user = userRepository.findByEmail(email);
        if (user == null || user.getOtp() == null) return false;
        if (user.getOtpExpiry() == null || user.getOtpExpiry().isBefore(LocalDateTime.now())) return false;
        if (!user.getOtp().equals(otp)) return false;
        user.setVerified(true);
        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);
        return true;
    }

    @Override
    public void resendOtp(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null || user.isVerified()) return;
        String otp = generateOtp();
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);
        sendOtpEmail(email, otp);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User save(UserDto userDto) {
        User user = new User(userDto.getUsername(),
                passwordEncoder.encode(userDto.getPassword()),
                userDto.getEmail());
        user.setRole(adminEmail.equalsIgnoreCase(userDto.getEmail()) ? "ADMIN" : "USER");
        return userRepository.save(user);
    }

    @Override
    public User saveExisting(User user) {
        return userRepository.save(user);
    }

    @Override
    public void updateUserRoles() {
        for (User user : userRepository.findAll()) {
            String role = adminEmail.equalsIgnoreCase(user.getEmail()) ? "ADMIN" : "USER";
            if (!role.equals(user.getRole())) {
                user.setRole(role);
                userRepository.save(user);
            }
        }
    }

    private String generateOtp() {
        return String.format("%06d", new SecureRandom().nextInt(1000000));
    }

    private void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom.isBlank() ? to : mailFrom);
        message.setTo(to);
        message.setSubject("CodeHub - Your verification code");
        message.setText("Your verification code is: " + otp + "\n\nThis code expires in 10 minutes.\n\nIf you did not register, ignore this email.");
        mailSender.send(message);
    }
}
