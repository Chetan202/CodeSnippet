package com.example.CodeHub.Services;

import com.example.CodeHub.Dto.SnippetDto;
import com.example.CodeHub.Entity.Snippet;
import com.example.CodeHub.Entity.User;
import com.example.CodeHub.Repository.SnippetRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SnippetServiceImpl implements SnippetService {

    private final SnippetRepository snippetRepository;

    public SnippetServiceImpl(SnippetRepository snippetRepository) {
        this.snippetRepository = snippetRepository;
    }

    @Override
    public Snippet save(SnippetDto snippetDto, User user) {
        Snippet snippet = new Snippet(
                snippetDto.getTitle(),
                snippetDto.getLanguage(),
                snippetDto.getCode(),
                user
        );
        return snippetRepository.save(snippet);
    }

    @Override
    public List<Snippet> findAllSnippets() {
        return snippetRepository.findAllByOrderByStarredDescCreatedAtDesc();
    }

    @Override
    public List<Snippet> findSnippetsByUser(User user) {
        return snippetRepository.findByUserOrderByStarredDescCreatedAtDesc(user);
    }

    @Override
    public Snippet findById(Long id) {
        return snippetRepository.findById(id).orElse(null);
    }

    @Override
    public Snippet update(Long id, SnippetDto snippetDto) {
        Snippet existingSnippet = findById(id);
        if (existingSnippet == null) {
            return null;
        }
        
        existingSnippet.setTitle(snippetDto.getTitle());
        existingSnippet.setLanguage(snippetDto.getLanguage());
        existingSnippet.setCode(snippetDto.getCode());
        
        return snippetRepository.save(existingSnippet);
    }

    @Override
    public void delete(Long id) {
        snippetRepository.deleteById(id);
    }

    @Override
    public void toggleStar(Long id, boolean starred) {
        Snippet snippet = findById(id);
        if (snippet != null) {
            snippet.setStarred(starred);
            snippetRepository.save(snippet);
        }
    }
    
    @Override
    public Page<Snippet> findAllSnippetsPaginated(Pageable pageable) {
        return snippetRepository.findAllByOrderByStarredDescCreatedAtDesc(pageable);
    }
    
    @Override
    public Page<Snippet> findSnippetsByUserPaginated(User user, Pageable pageable) {
        return snippetRepository.findByUserOrderByStarredDescCreatedAtDesc(user, pageable);
    }
    
    @Override
    public Page<Snippet> searchSnippets(String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findAllSnippetsPaginated(pageable);
        }
        return snippetRepository.searchSnippets(searchTerm.trim(), pageable);
    }
    
    @Override
    public Page<Snippet> searchUserSnippets(User user, String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findSnippetsByUserPaginated(user, pageable);
        }
        return snippetRepository.searchUserSnippets(user, searchTerm.trim(), pageable);
    }
}
