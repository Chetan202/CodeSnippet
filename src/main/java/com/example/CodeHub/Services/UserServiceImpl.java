package com.example.CodeHub.Services;

import com.example.CodeHub.Dto.UserDto;
import com.example.CodeHub.Entity.User;
import com.example.CodeHub.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Value("${app.admin-email:chetanjha888@gmail.com}")
    private String adminEmail;

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
        
        // Set ADMIN role for the specific email
        if (adminEmail.equalsIgnoreCase(userDto.getEmail())) {
            user.setRole("ADMIN");
        } else {
            user.setRole("USER");
        }
        
        return userRepository.save(user);
    }

    @Override
    public User saveExisting(User user) {
        return userRepository.save(user);
    }

    /**
     * Updates roles for all existing users based on their email address
     * Only the configured admin email will have admin privileges.
     */
    public void updateUserRoles() {
        Iterable<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            if (adminEmail.equalsIgnoreCase(user.getEmail())) {
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
