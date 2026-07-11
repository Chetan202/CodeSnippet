package com.example.CodeHub.Repository;

import com.example.CodeHub.Entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag findByNameIgnoreCase(String name);
    List<Tag> findAllByOrderByNameAsc();
}
