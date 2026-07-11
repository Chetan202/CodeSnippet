package com.example.CodeHub.Services;

import com.example.CodeHub.Dto.SnippetDto;
import com.example.CodeHub.Entity.Snippet;
import com.example.CodeHub.Entity.SnippetVersion;
import com.example.CodeHub.Entity.User;
import com.example.CodeHub.Repository.SnippetRepository;
import com.example.CodeHub.Repository.SnippetVersionRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SnippetServiceImpl implements SnippetService {

    private final SnippetRepository snippetRepository;
    private final SnippetVersionRepository snippetVersionRepository;

    public SnippetServiceImpl(SnippetRepository snippetRepository, SnippetVersionRepository snippetVersionRepository) {
        this.snippetRepository = snippetRepository;
        this.snippetVersionRepository = snippetVersionRepository;
    }

    @Override
    public Snippet save(SnippetDto snippetDto, User user) {
        Snippet snippet = new Snippet(
                snippetDto.getTitle(),
                snippetDto.getLanguage(),
                snippetDto.getCode(),
                user
        );
        applyEditableFields(snippet, snippetDto);
        return snippetRepository.save(snippet);
    }

    @Override
    public List<Snippet> findAllSnippets() {
        return snippetRepository.findAllByDeletedFalseOrderByStarredDescCreatedAtDesc();
    }

    @Override
    public long countSnippets() {
        return snippetRepository.countByDeletedFalse();
    }

    @Override
    public long countPublicSnippets() {
        return snippetRepository.countByPublicSnippetTrueAndDeletedFalse();
    }

    @Override
    public long countPrivateSnippets() {
        return snippetRepository.countByPublicSnippetFalseAndDeletedFalse();
    }

    @Override
    public List<Snippet> findSnippetsByUser(User user) {
        return snippetRepository.findByUserAndDeletedFalseOrderByStarredDescCreatedAtDesc(user);
    }

    @Override
    public Snippet findById(Long id) {
        return snippetRepository.findById(id)
                .filter(snippet -> !snippet.isDeleted())
                .orElse(null);
    }

    @Override
    public Snippet findPublicByShareToken(String shareToken) {
        return snippetRepository.findByShareTokenAndPublicSnippetTrueAndDeletedFalse(shareToken);
    }

    @Override
    public Snippet update(Long id, SnippetDto snippetDto) {
        Snippet existingSnippet = findById(id);
        if (existingSnippet == null) {
            return null;
        }

        snippetVersionRepository.save(new SnippetVersion(existingSnippet));
        applyEditableFields(existingSnippet, snippetDto);
        
        return snippetRepository.save(existingSnippet);
    }

    @Override
    public List<SnippetVersion> findVersions(Snippet snippet) {
        return snippetVersionRepository.findBySnippetOrderByCreatedAtDesc(snippet);
    }

    @Override
    public Snippet revertToVersion(Long snippetId, Long versionId) {
        Snippet snippet = findById(snippetId);
        if (snippet == null) {
            return null;
        }
        SnippetVersion version = snippetVersionRepository.findById(versionId).orElse(null);
        if (version == null || !version.getSnippet().getId().equals(snippet.getId())) {
            return null;
        }
        snippetVersionRepository.save(new SnippetVersion(snippet));
        snippet.setTitle(version.getTitle());
        snippet.setLanguage(version.getLanguage());
        snippet.setTags(version.getTags());
        snippet.setCollectionName(version.getCollectionName());
        snippet.setCode(version.getCode());
        return snippetRepository.save(snippet);
    }

    @Override
    public void delete(Long id) {
        Snippet snippet = findById(id);
        if (snippet != null) {
            snippet.setDeleted(true);
            snippet.setDeletedAt(LocalDateTime.now());
            snippetRepository.save(snippet);
        }
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
        return snippetRepository.findAllByDeletedFalseOrderByStarredDescCreatedAtDesc(pageable);
    }
    
    @Override
    public Page<Snippet> findSnippetsByUserPaginated(User user, Pageable pageable) {
        return snippetRepository.findByUserAndDeletedFalseOrderByStarredDescCreatedAtDesc(user, pageable);
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

    private void applyEditableFields(Snippet snippet, SnippetDto snippetDto) {
        snippet.setTitle(snippetDto.getTitle());
        snippet.setLanguage(snippetDto.getLanguage());
        snippet.setCode(snippetDto.getCode());
        snippet.setTags(snippetDto.getTags());
        snippet.setCollectionName(snippetDto.getCollectionName());
        snippet.setPublicSnippet(snippetDto.isPublicSnippet());
    }
}
