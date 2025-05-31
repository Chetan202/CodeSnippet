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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final SnippetService snippetService;
    private final UserViewService userViewService;

    public AdminController(UserRepository userRepository, SnippetService snippetService, UserViewService userViewService) {
        this.userRepository = userRepository;
        this.snippetService = snippetService;
        this.userViewService = userViewService;
    }

    @GetMapping("/statistics")
    public String showStatistics(Model model) {
        // Get basic statistics for admin view
        Iterable<User> usersIterable = userRepository.findAll();
        List<Snippet> snippets = snippetService.findAllSnippets();
        
        // Count total users
        long totalUsers = 0;
        long verifiedUsers = 0;
        
        for (User user : usersIterable) {
            totalUsers++;
            if (user.isVerified()) {
                verifiedUsers++;
            }
        }
        
        // Get view statistics data for the chart
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(14); // Show last 14 days
        Map<LocalDate, Long> viewCounts = userViewService.getViewCountsByDateRange(startDate, endDate);
        
        // Convert view data to JSON format for the chart
        List<String> viewDates = new ArrayList<>();
        List<Long> viewCountsList = new ArrayList<>();
        
        for (Map.Entry<LocalDate, Long> entry : viewCounts.entrySet()) {
            viewDates.add(entry.getKey().toString());
            viewCountsList.add(entry.getValue());
        }
        
        // Add statistics to model
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("verifiedUsers", verifiedUsers);
        model.addAttribute("totalSnippets", snippets.size());
        model.addAttribute("snippets", snippets);
        model.addAttribute("viewDates", viewDates);
        model.addAttribute("viewCounts", viewCountsList);
        
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
