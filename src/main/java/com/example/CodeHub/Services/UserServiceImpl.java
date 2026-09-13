package com.example.CodeHub.Services;

import com.example.CodeHub.Dto.UserDto;
import com.example.CodeHub.Entity.User;
import com.example.CodeHub.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           @Value("${app.admin-email:chetanjha888@gmail.com}") String adminEmail) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
    }

    @Override
    public User registerUser(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()) != null) {
            throw new IllegalArgumentException("Email already registered");
        }
        User user = new User(userDto.getEmail(),
                passwordEncoder.encode(userDto.getPassword()),
                userDto.getEmail());
        user.setRole(adminEmail.equalsIgnoreCase(userDto.getEmail()) ? "ADMIN" : "USER");
        user.setVerified(true);
        userRepository.save(user);
        return user;
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
        User user = new User(userDto.getEmail(),
                passwordEncoder.encode(userDto.getPassword()),
                userDto.getEmail());
        user.setRole(adminEmail.equalsIgnoreCase(userDto.getEmail()) ? "ADMIN" : "USER");
        user.setVerified(true);
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
}
