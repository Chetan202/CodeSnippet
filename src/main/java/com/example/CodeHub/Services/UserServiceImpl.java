package com.example.CodeHub.Services;

import com.example.CodeHub.Dto.UserDto;
import com.example.CodeHub.Entity.User;
import com.example.CodeHub.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    PasswordEncoder passwordEncoder;

    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        super();
        this.userRepository = userRepository;
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
        User user = new User(userDto.getUsername(), passwordEncoder.encode(userDto.getPassword()),
                userDto.getEmail());
        user.setVerified(false); // Set default verification status to false
        
        // Set ADMIN role for the specific email
        if ("chetanjha888@gmail.com".equalsIgnoreCase(userDto.getEmail())) {
            user.setRole("ADMIN");
        } else {
            user.setRole("USER");
        }
        
        return userRepository.save(user);
    }

    @Override
    public String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        userRepository.save(user);
        return token;
    }

    @Override
    public boolean verifyUser(String token) {
        User user = findByVerificationToken(token);
        if (user != null) {
            // Check if user is already verified
            if (user.isVerified()) {
                // Token has already been used, return false to indicate token expired
                return false;
            }
            
            // Mark user as verified and clear the token
            user.setVerified(true);
            user.setVerificationToken(null); // Clear token after verification
            userRepository.save(user);
            return true;
        }
        // Token not found
        return false;
    }

    @Override
    public User findByVerificationToken(String token) {
        return userRepository.findByVerificationToken(token);
    }
    
    /**
     * Updates roles for all existing users based on their email address
     * Only the user with email chetanjha888@gmail.com will have admin privileges
     */
    public void updateUserRoles() {
        Iterable<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            if ("chetanjha888@gmail.com".equalsIgnoreCase(user.getEmail())) {
                user.setRole("ADMIN");
            } else {
                // Make sure everyone else has USER role
                if (!"USER".equals(user.getRole())) {
                    user.setRole("USER");
                }
            }
            userRepository.save(user);
        }
    }
}