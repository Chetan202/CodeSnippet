package com.example.CodeHub.Controller;

import com.example.CodeHub.Entity.Snippet;
import com.example.CodeHub.Entity.User;
import com.example.CodeHub.Repository.UserRepository;
import com.example.CodeHub.Services.SnippetService;
import com.example.CodeHub.Services.UserViewService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.time.LocalDate;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final SnippetService snippetService;
    private final UserViewService userViewService;

    public AdminController(UserRepository userRepository, SnippetService snippetService,
                           UserViewService userViewService) {
        this.userRepository = userRepository;
        this.snippetService = snippetService;
        this.userViewService = userViewService;
    }

    @GetMapping("/statistics")
    public String statistics(Model model) {
        LocalDate today = LocalDate.now();
        model.addAttribute("users", userRepository.findAllByOrderByCreatedAtDesc());
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("verifiedUsers", userRepository.countByIsVerifiedTrue());
        model.addAttribute("pendingUsers", userRepository.countByIsVerifiedFalse());
        model.addAttribute("totalSnippets", snippetService.countSnippets());
        model.addAttribute("todayActiveUsers", userViewService.getViewCountForDate(today));
        model.addAttribute("recentActivity", userViewService.getViewCountsByDateRange(today.minusDays(6), today));
        return "admin-statistics";
    }
    
    @GetMapping("/all-snippets")
    public String viewAllSnippets(Model model) {
        // Get all snippets for admin view
        List<Snippet> snippets = snippetService.findAllSnippets();
        model.addAttribute("snippets", snippets);
        model.addAttribute("adminView", true);
        
        // Get current user for the header
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            User user = userRepository.findByUsername(auth.getName());
            if (user != null) {
                model.addAttribute("user", user);
            }
        }
        
        return "home";
    }
}
