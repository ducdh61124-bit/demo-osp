package com.example.demo.service;

import java.util.*;

import com.example.demo.entity.Book;
import com.example.demo.entity.Category;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private HistoryService historyService;

    public List<Category> findAll(){
        return categoryRepository.findAll();
    }

    public Category findCategoryById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("category.notfound", id));
    }

    public Category saveCategory(Category category){
        boolean isNew = (category.getId() == null);

        if (isNew) {
            if (categoryRepository.existsByName(category.getName())) {
                throw new RuntimeException("category.name.exists");
            }
        } else {
            if (categoryRepository.existsByNameAndIdNot(category.getName(), category.getId())) {
                throw new RuntimeException("category.name.exists");
            }
        }

        if (category.getBooks() != null) {
            for (Book book : category.getBooks()) {
                book.setCategory(category);
            }
        }

        Category savedCategory = categoryRepository.save(category);

        if (isNew) {
            historyService.logAction("CREATE", "CATEGORY", savedCategory.getName(), "admin", "Tạo mới danh mục: " + savedCategory.getName());
        } else {
            historyService.logAction("UPDATE", "CATEGORY", savedCategory.getName(), "admin", "Cập nhật danh mục: " + savedCategory.getName());
        }

        return savedCategory;
    }

    public void deleteCategoryById(Integer id){
        categoryRepository.findById(id).ifPresent(category -> {
            categoryRepository.deleteById(id);
            historyService.logAction("DELETE", "CATEGORY", category.getName(), "admin", "Xóa danh mục: " + category.getName());
        });
    }

}
