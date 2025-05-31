package com.example.CodeHub.Controller;

import com.example.CodeHub.Dto.SnippetDto;
import com.example.CodeHub.Entity.Snippet;
import com.example.CodeHub.Entity.User;
import com.example.CodeHub.Repository.UserRepository;
import com.example.CodeHub.Services.SnippetService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



@Controller
public class SnippetController {

    private final SnippetService snippetService;
    private final UserRepository userRepository;

    public SnippetController(SnippetService snippetService, UserRepository userRepository) {
        this.snippetService = snippetService;
        this.userRepository = userRepository;
    }

    @GetMapping("/snippets/create")
    public String showCreateForm(Model model) {
        model.addAttribute("snippet", new SnippetDto());
        return "create-snippet";
    }

    @PostMapping("/snippets/create")
    public String createSnippet(@ModelAttribute("snippet") SnippetDto snippetDto, 
                              RedirectAttributes redirectAttributes) {
        // Get current authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // Check if user is authenticated (not anonymous)
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            User user = userRepository.findByUsername(auth.getName());
            
            if (user != null) {
                // Save the snippet associated with the logged-in user
                Snippet savedSnippet = snippetService.save(snippetDto, user);
                
                // Add success message
                redirectAttributes.addFlashAttribute("snippetCreated", true);
                redirectAttributes.addFlashAttribute("snippetId", savedSnippet.getId());
                
                // For URL parameter approach as a fallback
                return "redirect:/home?snippetCreated=true";
            }
        }
        
        // If not authenticated or user not found, redirect to login
        redirectAttributes.addFlashAttribute("loginError", "You must be logged in to create snippets.");
        return "redirect:/login";
    }
    
    @GetMapping("/snippets/{id}")
    public String viewSnippet(@PathVariable Long id, Model model) {
        // Check if user is authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "redirect:/login";
        }
        
        // Get the current user
        User currentUser = userRepository.findByUsername(auth.getName());
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        // Get the snippet
        Snippet snippet = snippetService.findById(id);
        if (snippet == null) {
            return "redirect:/home";
        }
        
        // Check if the current user is the owner of the snippet
        if (!snippet.getUser().getId().equals(currentUser.getId())) {
            // User is not the owner, redirect to home
            return "redirect:/home?error=unauthorized";
        }
        
        model.addAttribute("snippet", snippet);
        return "view-snippet";
    }

    @GetMapping("/snippets/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "redirect:/login";
        }
        
        Snippet snippet = snippetService.findById(id);
        if (snippet == null) {
            return "redirect:/home";
        }
        
        // Check if user is the owner of the snippet
        User currentUser = userRepository.findByUsername(auth.getName());
        if (!snippet.getUser().getId().equals(currentUser.getId())) {
            return "redirect:/home";  // User is not the owner
        }
        
        SnippetDto snippetDto = new SnippetDto();
        snippetDto.setTitle(snippet.getTitle());
        snippetDto.setLanguage(snippet.getLanguage());
        snippetDto.setCode(snippet.getCode());
        
        model.addAttribute("snippet", snippetDto);
        model.addAttribute("snippetId", id);
        return "edit-snippet";
    }
    
    @PostMapping("/snippets/{id}/update")
    public String updateSnippet(@PathVariable Long id, @ModelAttribute("snippet") SnippetDto snippetDto,
                               RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "redirect:/login";
        }
        
        User currentUser = userRepository.findByUsername(auth.getName());
        Snippet snippet = snippetService.findById(id);
        
        if (snippet == null || !snippet.getUser().getId().equals(currentUser.getId())) {
            return "redirect:/home";  // Snippet not found or user not owner
        }
        
        snippetService.update(id, snippetDto);
        redirectAttributes.addFlashAttribute("snippetUpdated", true);
        return "redirect:/home";
    }
    
    @PostMapping("/snippets/{id}/delete")
    public String deleteSnippet(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "redirect:/login";
        }
        
        // Get the current user
        User currentUser = userRepository.findByUsername(auth.getName());
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        // Get the snippet
        Snippet snippet = snippetService.findById(id);
        if (snippet == null) {
            return "redirect:/home?error=notfound";  // Snippet not found
        }
        
        // Check if the current user is the owner of the snippet
        if (!snippet.getUser().getId().equals(currentUser.getId())) {
            return "redirect:/home?error=unauthorized";  // User not owner
        }
        
        snippetService.delete(id);
        redirectAttributes.addFlashAttribute("snippetDeleted", true);
        return "redirect:/home";
    }
    
    @PostMapping("/snippets/{id}/star")
    public String toggleStarSnippet(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return "redirect:/login";
        }
        
        // Get the current user
        User currentUser = userRepository.findByUsername(auth.getName());
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        // Get the snippet
        Snippet snippet = snippetService.findById(id);
        if (snippet == null) {
            return "redirect:/home";  // Snippet not found
        }
        
        // Check if the current user is the owner of the snippet
        if (!snippet.getUser().getId().equals(currentUser.getId())) {
            // User is not the owner, redirect to home
            return "redirect:/home?error=unauthorized";
        }
        
        // Toggle the starred status
        boolean newStarredStatus = !snippet.isStarred();
        snippetService.toggleStar(id, newStarredStatus);
        
        redirectAttributes.addFlashAttribute("snippetStarred", newStarredStatus);
        return "redirect:/home";
    }
    
    /**
     * REST endpoint for search suggestions
     * Returns snippet previews that match the search term
     * Only returns the user's own snippets if authenticated
     */
    @GetMapping("/api/search-suggestions")
    @ResponseBody
    public ResponseEntity<List<Map<String, String>>> getSearchSuggestions(@RequestParam("term") String searchTerm) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Snippet> suggestions;
        
        // Limit to 5 results for performance and better UX
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 5);
        
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            // User is authenticated, only show their snippets
            User currentUser = userRepository.findByUsername(auth.getName());
            if (currentUser != null) {
                // Get suggestions for the current user only
                suggestions = snippetService.searchUserSnippets(currentUser, searchTerm, pageable).getContent();
            } else {
                // User not found in database, return empty list
                return ResponseEntity.ok(List.of());
            }
        } else {
            // Not authenticated, return empty list
            return ResponseEntity.ok(List.of());
        }
        
        // Create preview data for each snippet
        List<Map<String, String>> previewData = suggestions.stream()
                .map(snippet -> {
                    Map<String, String> preview = new HashMap<>();
                    preview.put("id", snippet.getId().toString());
                    preview.put("title", snippet.getTitle());
                    preview.put("language", snippet.getLanguage());
                    
                    // Get a short preview of the code (first 100 chars)
                    String codePreview = snippet.getCode();
                    if (codePreview.length() > 100) {
                        codePreview = codePreview.substring(0, 100) + "...";
                    }
                    preview.put("codePreview", codePreview);
                    
                    return preview;
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(previewData);
    }
}
