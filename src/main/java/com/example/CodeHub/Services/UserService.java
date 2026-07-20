package com.example.CodeHub.Services;

import com.example.CodeHub.Dto.UserDto;
import com.example.CodeHub.Entity.User;

public interface UserService {
    User findByUsername(String username);
    User findByEmail(String email);
    User save(UserDto userDto);
    User saveExisting(User user);
    void updateUserRoles();
    User registerUser(UserDto userDto);
    boolean verifyOtp(String email, String otp);
    void resendOtp(String email);
}
