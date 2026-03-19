package com.example.demo.configuration;

import com.example.demo.entity.*;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DataInitializer {

    @Bean
    CommandLineRunner  initDatabase(UserService userService, CategoryService categoryService, BookService bookService){
        return args ->{
            // 1. Khởi tạo 10 User
            if (userService.getAllUsers().isEmpty()){
                System.out.println(">> Đang thêm 10 Users mẫu");
                for(int i = 1; i <= 10; i++ ){
                    User user = new User();
                    String username = (i == 1) ? "admin" : "user" + i;
                    user.setUsername(username);
                    user.setPassword("123456");
                    user.setName(i == 1 ? "Quản Trị Viên" : "Khách Hàng " + i);
                    user.setEmail(username + "@gmail.com");
                    user.setPhone("098765432" + (i - 1));
                    userService.createUser(user);
                }
            }

            // 2. Khởi tạo 10 Category
            List<Category> cats = new ArrayList<>();
            if (categoryService.findAll().isEmpty()) {
                System.out.println(">> Đang bơm 10 Danh mục mẫu...");
                String[] names = {"Công nghệ", "Văn học", "Kinh tế", "Kỹ năng", "Ngoại ngữ", "Thiếu nhi", "Tâm lý", "Lịch sử", "Khoa học", "Nghệ thuật"};
                for (String n : names) {
                    Category c = new Category();
                    c.setName(n);
                    c.setDescription("Kho sách chuyên về lĩnh vực " + n + " cực kỳ hấp dẫn.");
                    c.setStatus(true);
                    cats.add(categoryService.saveCategory(c));
                }
            } else {
                cats = categoryService.findAll();
            }

            // 3. Khởi tạo 10 Book
            if (bookService.findAllByOrderByTitleAsc().isEmpty() && !cats.isEmpty()) {
                System.out.println(">> Đang thêm 10 Cuốn sách mẫu...");
                String[][] bookData = {
                        {"Clean Code", "Robert C. Martin"},
                        {"Design Patterns", "Gang of Four"},
                        {"Java Core", "Cay S. Horstmann"},
                        {"Spring Boot In Action", "Craig Walls"},
                        {"Dế Mèn Phiêu Lưu Ký", "Tô Hoài"},
                        {"Đắc Nhân Tâm", "Dale Carnegie"},
                        {"Cha Giàu Cha Nghèo", "Robert Kiyosaki"},
                        {"Lược Sử Thời Gian", "Stephen Hawking"},
                        {"Tư Duy Nhanh Và Chậm", "Daniel Kahneman"},
                        {"Trí Tuệ Do Thái", "Eran Katz"}
                };

                for (int i = 0; i < 10; i++) {
                    Book b = new Book();
                    b.setTitle(bookData[i][0]);
                    b.setAuthor(bookData[i][1]);
                    b.setPrice(100000.0 + (i * 25000));
                    b.setStock(50L + i);
                    b.setCategory(cats.get(i % cats.size()));
                    b.setImage(null);
                    bookService.saveBook(b);
                }

                System.out.println(">> HỆ THỐNG ĐÃ SẴN SÀNG VỚI DỮ LIỆU CHUẨN! <<");
            }
        };
    }
}
