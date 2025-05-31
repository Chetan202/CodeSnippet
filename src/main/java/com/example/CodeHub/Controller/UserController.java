package com.example.CodeHub.Controller;

import com.example.CodeHub.Dto.UserDto;
import com.example.CodeHub.Entity.User;
import com.example.CodeHub.Repository.UserRepository;
import com.example.CodeHub.Services.EmailService;
import com.example.CodeHub.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.HashMap;
import java.util.Map;

import java.security.Principal;

import java.security.Principal;

@Controller
public class UserController {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private EmailService emailService;

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user-dashboard")
    public String userDashboard(Model model, Principal principal) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
        model.addAttribute("userdetail", userDetails);
        return "home";
    }

    @GetMapping("/login")
    public String login(Model model, UserDto userDto) {

        model.addAttribute("user", userDto);
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model, UserDto userDto) {
        model.addAttribute("user", userDto);
        return "register";
    }

    @PostMapping("/register")
    public String registerSava(@ModelAttribute("user") UserDto userDto, Model model) {
        // Check if username exists
        User userByUsername = userService.findByUsername(userDto.getUsername());
        if (userByUsername != null) {
            model.addAttribute("Userexist", userByUsername);
            return "register";
        }
        
        // Check if email exists
        User userByEmail = userService.findByEmail(userDto.getEmail());
        if (userByEmail != null) {
            model.addAttribute("emailExists", true);
            return "register";
        }
        
        // Save the user
        User savedUser = userService.save(userDto);

        // Generate verification token
        String token = userService.generateVerificationToken(savedUser);

        // Send verification email
        emailService.sendVerificationEmail(savedUser.getEmail(), token);

        return "redirect:/register?success";
    }

    @GetMapping("/check-email")
    @ResponseBody
    public Map<String, Boolean> checkEmailExists(@RequestParam String email) {
        User user = userService.findByEmail(email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", user != null);
        return response;
    }
    
    @GetMapping("/verify")
    public String verifyAccount(@RequestParam("token") String token, Model model) {
        // First check if the token exists
        User user = userService.findByVerificationToken(token);
        
        if (user == null) {
            model.addAttribute("message", "Invalid verification token. The token may have expired or been used already.");
            model.addAttribute("status", "error");
        } else if (user.isVerified()) {
            model.addAttribute("message", "This account has already been verified. The verification token has expired.");
            model.addAttribute("status", "warning");
        } else {
            // Valid token and user not yet verified, proceed with verification
            boolean verified = userService.verifyUser(token);
            
            if (verified) {
                model.addAttribute("message", "Your account has been successfully verified. You can now login.");
                model.addAttribute("status", "success");
            } else {
                model.addAttribute("message", "Failed to verify your account. Please try again or contact support.");
                model.addAttribute("status", "error");
            }
        }

        return "verification";
    }
//    @PostMapping
//    public ResponseEntity<String> registerSava(@ModelAttribute("user") UserDto userDto, Model model) {
//        User user = UserRepository.findByEmail(userDto.getEmail());
//        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
//        UserRepository.
//    }
}