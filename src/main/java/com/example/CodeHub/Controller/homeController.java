package com.example.CodeHub.Controller;

import com.example.CodeHub.Entity.Snippet;
import com.example.CodeHub.Entity.User;
import com.example.CodeHub.Repository.UserRepository;
import com.example.CodeHub.Services.SnippetService;
import com.example.CodeHub.Services.UserViewService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
public class homeController {
    
    private final SnippetService snippetService;
    private final UserRepository userRepository;
    private final UserViewService userViewService;
    
    public homeController(SnippetService snippetService, UserRepository userRepository, UserViewService userViewService) {
        this.snippetService = snippetService;
        this.userRepository = userRepository;
        this.userViewService = userViewService;
    }
    
    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/home";
    }
    
    @GetMapping("/home")
    public String home(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "search", required = false) String searchTerm,
            @RequestParam(name = "error", required = false) String error,
            Model model) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // Add error message if provided (for both authenticated and unauthenticated users)
        if (error != null) {
            if (error.equals("unauthorized")) {
                model.addAttribute("errorMessage", "You don't have permission to access that snippet.");
            } else if (error.equals("notfound")) {
                model.addAttribute("errorMessage", "The requested snippet was not found.");
            }
        }
        
        // If not authenticated, show welcome page (controlled by sec:authorize in template)
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            return "home";
        }
        
        // User is authenticated, get their snippets
        User user = userRepository.findByUsername(auth.getName());
        if (user == null) {
            // This shouldn't happen normally, but just in case
            return "redirect:/login";
        }
        
        // Record the user's visit for statistics tracking
        userViewService.recordView(user);
        model.addAttribute("user", user);
        
        // Set up pagination
        Pageable pageable = PageRequest.of(page, size);
        Page<Snippet> snippetPage;
        
        // Get paginated snippets for the current user with optional search
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            snippetPage = snippetService.searchUserSnippets(user, searchTerm, pageable);
            model.addAttribute("searchTerm", searchTerm);
        } else {
            snippetPage = snippetService.findSnippetsByUserPaginated(user, pageable);
        }
        
        // Add a flag to indicate we're showing only user's snippets
        model.addAttribute("userSnippetsOnly", true);
        
        model.addAttribute("snippets", snippetPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", snippetPage.getTotalPages());
        model.addAttribute("totalItems", snippetPage.getTotalElements());
        
        return "home";
    }
    
    @GetMapping("/api/search-snippets")
    @ResponseBody
    public Map<String, Object> searchSnippets(
            @RequestParam(name = "search", required = false) String searchTerm,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        
        // If not authenticated, return empty results
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            response.put("snippets", List.of());
            response.put("currentPage", 0);
            response.put("totalPages", 0);
            response.put("totalItems", 0);
            response.put("error", "Authentication required");
            return response;
        }
        
        // User is authenticated, get their snippets
        User user = userRepository.findByUsername(auth.getName());
        if (user == null) {
            // This shouldn't happen normally, but just in case
            response.put("snippets", List.of());
            response.put("currentPage", 0);
            response.put("totalPages", 0);
            response.put("totalItems", 0);
            response.put("error", "User not found");
            return response;
        }
        
        // Set up pagination
        Pageable pageable = PageRequest.of(page, size);
        Page<Snippet> snippetPage;
        
        // Get paginated snippets for the current user with optional search
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            snippetPage = snippetService.searchUserSnippets(user, searchTerm, pageable);
        } else {
            snippetPage = snippetService.findSnippetsByUserPaginated(user, pageable);
        }
        
        // Add snippet data to the response
        response.put("snippets", snippetPage.getContent());
        response.put("currentPage", page);
        response.put("totalPages", snippetPage.getTotalPages());
        response.put("totalItems", snippetPage.getTotalElements());
        
        return response;
    }
}
