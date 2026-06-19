package com.example.CodeHub.Controller;

import com.example.CodeHub.Entity.Snippet;
import com.example.CodeHub.Entity.User;
import com.example.CodeHub.Repository.UserRepository;
import com.example.CodeHub.Services.SnippetService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final SnippetService snippetService;

    public AdminController(UserRepository userRepository, SnippetService snippetService) {
        this.userRepository = userRepository;
        this.snippetService = snippetService;
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
