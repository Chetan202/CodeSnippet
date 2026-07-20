package com.example.CodeHub.Controller;

import com.example.CodeHub.Dto.UserDto;
import com.example.CodeHub.Services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

@Controller
public class UserController {

    private final UserDetailsService userDetailsService;
    private final UserService userService;

    public UserController(UserDetailsService userDetailsService, UserService userService) {
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }

    @GetMapping("/user-dashboard")
    public String userDashboard(Model model, Principal principal) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
        model.addAttribute("userdetail", userDetails);
        return "home";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("user", new UserDto());
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new UserDto());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") UserDto userDto, Model model) {
        if (userDto.getPassword() == null || !userDto.getPassword().equals(userDto.getConfirmPassword())) {
            model.addAttribute("error", "Passwords do not match");
            return "register";
        }
        try {
            userService.registerUser(userDto);
            String encodedEmail = URLEncoder.encode(userDto.getEmail(), StandardCharsets.UTF_8);
            return "redirect:/verify-otp?email=" + encodedEmail;
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/verify-otp")
    public String verifyOtpPage(@RequestParam String email,
                                @RequestParam(required = false) Boolean notverified,
                                Model model) {
        model.addAttribute("email", email);
        if (Boolean.TRUE.equals(notverified)) {
            model.addAttribute("info", "Please verify your email before logging in. Enter the OTP sent to " + email);
        }
        return "verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String email, @RequestParam String otp, Model model) {
        if (userService.verifyOtp(email, otp)) {
            return "redirect:/login?verified";
        }
        model.addAttribute("email", email);
        model.addAttribute("error", "Invalid or expired OTP. Please try again.");
        return "verify-otp";
    }

    @PostMapping("/resend-otp")
    public String resendOtp(@RequestParam String email, Model model) {
        userService.resendOtp(email);
        model.addAttribute("email", email);
        model.addAttribute("info", "A new OTP has been sent to " + email);
        return "verify-otp";
    }
}
