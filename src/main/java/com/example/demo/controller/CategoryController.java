package com.example.demo.controller;

import com.example.demo.entity.Category;
import com.example.demo.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private MessageSource messageSource;

    // 1. Lấy danh sách thể loại sách - GET
    @GetMapping
    public List<Category> getAllCategory(){
        return categoryService.findAll();
    }

    // 2. Lấy chi tiết - GET
    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable Integer id) {
        return categoryService.findCategoryById(id);
    }

    // 3. Thêm thể loại sách - POST
    @PostMapping
    public ResponseEntity<String> saveCategory(@RequestBody Category category) {
        Category savedCategory = categoryService.saveCategory(category);
        String msg = messageSource.getMessage("category.create.success", null, LocaleContextHolder.getLocale());
        return ResponseEntity.ok(msg + ": " + savedCategory.getName());
    }

    // 4. Sửa thể loại sách - PUT
    @PutMapping("/{id}")
    public ResponseEntity<String> updateCategory(@RequestBody Category newInfo, @PathVariable Integer id) {
        Category categoryCurrently = categoryService.findCategoryById(id);

        if (newInfo.getName() != null) categoryCurrently.setName(newInfo.getName());
        if (newInfo.getDescription() != null) categoryCurrently.setDescription(newInfo.getDescription());
        if (newInfo.getStatus() != null) categoryCurrently.setStatus(newInfo.getStatus());

        categoryService.saveCategory(categoryCurrently);
        String msg = messageSource.getMessage("category.update.success", null, LocaleContextHolder.getLocale());
        return ResponseEntity.ok(msg);
    }

    // 4. Xóa thể loại sách - DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategoryById(@PathVariable Integer id) {
        categoryService.findCategoryById(id);
        categoryService.deleteCategoryById(id);
        String msg = messageSource.getMessage("category.delete.success", null, LocaleContextHolder.getLocale());
        return ResponseEntity.ok(msg);
    }
}
