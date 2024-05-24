package com.example.travelDiary.repository.persistence.tags;

import com.example.travelDiary.domain.model.tags.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findAllByNameContaining(String categoryName);

    Category findByName(String name);
}