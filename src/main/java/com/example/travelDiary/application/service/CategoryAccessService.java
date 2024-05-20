package com.example.travelDiary.application.service;

import com.example.travelDiary.domain.repository.tags.CategoryRepository;
import com.example.travelDiary.domain.tags.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryAccessService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryAccessService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Category> getAllCategoriesByCategoryName(String categoryName) {
        return categoryRepository.findAllByNameContaining(categoryName);
    }

    public Category updateCategory(Category category) {
        Category originalCategory = categoryRepository.findByName(category.name);
        originalCategory.setName(category.name);
        originalCategory.setAbbrev(category.abbrev);
        return categoryRepository.save(originalCategory);
    }

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public void deleteCategory(Category category) {
        categoryRepository.delete(category);
    }
}
