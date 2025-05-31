package com.example.CodeHub.Services;

import com.example.CodeHub.Dto.UserDto;
import com.example.CodeHub.Entity.User;

public interface UserService {
    User findByUsername(String username);

    User findByEmail(String email);

    User save(UserDto userDto);
    
    String generateVerificationToken(User user);
    
    boolean verifyUser(String token);
    
    User findByVerificationToken(String token);
    
    void updateUserRoles();
}