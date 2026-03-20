package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.example.demo.entity.*;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock private CategoryRepository categoryRepository;
    @InjectMocks private CategoryService categoryService;
    @Mock private HistoryService historyService;

    @Test void findAll_ShouldReturnList() {
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(new Category(), new Category()));
        assertEquals(2, categoryService.findAll().size());
    }
        @Test void findById_Success() {
        Category c = new Category(); c.setId(1);
        when(categoryRepository.findById(1)).thenReturn(Optional.of(c));
        assertNotNull(categoryService.findCategoryById(1));
    }
    @Test void findById_NotFound() {
        when(categoryRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> categoryService.findCategoryById(99));
    }
    @Test void save_New_Success() {
        Category c = new Category(); c.setName("IT");
        when(categoryRepository.existsByName("IT")).thenReturn(false);
        when(categoryRepository.save(any())).thenReturn(c);
        assertNotNull(categoryService.saveCategory(c));
    }
    @Test void save_New_NameExists() {
        Category c = new Category(); c.setName("IT");
        when(categoryRepository.existsByName("IT")).thenReturn(true);
        assertThrows(RuntimeException.class, () -> categoryService.saveCategory(c));
    }
    @Test void save_Update_Success() {
        Category c = new Category(); c.setId(1); c.setName("IT");
        when(categoryRepository.existsByNameAndIdNot("IT", 1)).thenReturn(false);
        when(categoryRepository.save(any())).thenReturn(c);
        assertNotNull(categoryService.saveCategory(c));
    }
    @Test void save_Update_DuplicateName() {
        Category c = new Category(); c.setId(1); c.setName("IT");
        when(categoryRepository.existsByNameAndIdNot("IT", 1)).thenReturn(true);
        assertThrows(RuntimeException.class, () -> categoryService.saveCategory(c));
    }
    @Test void save_WithBooks_ShouldRelink() {
        Category c = new Category(); c.setName("IT");
        Book b = new Book();
        c.setBooks(Collections.singletonList(b));
        when(categoryRepository.save(any())).thenReturn(c);
        categoryService.saveCategory(c);
        assertEquals(c, b.getCategory());
    }
    @Test void delete_Success() {
        Category c = new Category(); c.setId(1); c.setName("IT");
        when(categoryRepository.findById(1)).thenReturn(Optional.of(c));
        categoryService.deleteCategoryById(1);
        verify(categoryRepository).deleteById(1);
        verify(historyService).logAction(any(), any(), any(), any(), any());
    }
    @Test void findAll_Empty() {
        when(categoryRepository.findAll()).thenReturn(new ArrayList<>());
        assertTrue(categoryService.findAll().isEmpty());
    }
}