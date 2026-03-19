package com.example.demo.controller;

import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.UserService;

import com.example.demo.repository.UserRepository;
import com.example.demo.service.EmailService;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    private Map<String, Object> createResponse(String messageKey, Object data) {
        String msg = messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
        Map<String, Object> response = new HashMap<>();
        response.put("message", msg);
        if (data != null) response.put("data", data);
        return response;
    }

    // 1. POST - ĐĂNG NHẬP

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        User user = userService.checkLogin(username, password);

        if (user != null) {
            return ResponseEntity.ok(createResponse("login.success", user));
        }

        String errorMsg = messageSource.getMessage("login.fail", null, LocaleContextHolder.getLocale());
        return ResponseEntity.status(401).body(errorMsg);
    }

    // 2. GET - LẤY DANH SÁCH TOÀN BỘ USER

    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // 3. POST - TẠO MỚI (ĐĂNG KÝ)

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User newUser) {
        try {
            User savedUser = userService.createUser(newUser);
            return ResponseEntity.ok(createResponse("user.register.success", savedUser));
        } catch (RuntimeException e) {
            String errorMsg = messageSource.getMessage(e.getMessage(), null, LocaleContextHolder.getLocale());
            return ResponseEntity.badRequest().body(errorMsg);
        }
    }

    // 4. API MỚI: YÊU CẦU GỬI MÃ OTP VÀO EMAIL

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {

        String email = (request != null && request.get("email") != null)
                ? request.get("email").trim() : "";
        List<User> allUsers = userRepository.findAll();
        allUsers.forEach(u -> System.out.println("   -> [" + u.getEmail() + "]"));

        if (email.isEmpty()) {
            return ResponseEntity.badRequest().body("Vui lòng nhập Email!");
        }

        Optional<User> userOptional = userRepository.findByEmailIgnoreCase(email);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(400).body("Email không tồn tại trong hệ thống!");
        }

        User user = userOptional.get();

        String otp = String.format("%06d", new Random().nextInt(999999));
        user.setOtpCode(otp);
        user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);

        try {
            emailService.sendOtpEmail(user.getEmail(), otp);
            Map<String, Object> successRes = new HashMap<>();
            successRes.put("message", "Mã OTP đã được gửi thành công!");
            return ResponseEntity.ok(successRes);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi gửi mail: " + e.getMessage());
        }
    }

    // 5. API MỚI: KIỂM TRA OTP VÀ ĐỔI MẬT KHẨU

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email") != null ? request.get("email").trim() : "";
        String otp = request.get("otp");
        String newPassword = request.get("newPassword");

        Optional<User> userOptional = userRepository.findByEmailIgnoreCase(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(400).body("Email không hợp lệ!");
        }

        User user = userOptional.get();

        if (user.getOtpCode() == null || !user.getOtpCode().equals(otp)) {
            return ResponseEntity.status(400).body("Mã OTP không chính xác!");
        }

        if (user.getOtpExpiryTime().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(400).body("Mã OTP đã hết hạn!");
        }

        user.setPassword(newPassword);
        user.setOtpCode(null);
        user.setOtpExpiryTime(null);
        userRepository.save(user);

        return ResponseEntity.ok(createResponse("password.reset.success", null));
    }

    // 6. PUT - CẬP NHẬT THÔNG TIN

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody User updatedInfo) {
        try {
            User updatedUser = userService.updateUser(id, updatedInfo);
            return ResponseEntity.ok(createResponse("user.update.success", updatedUser));

        } catch (ResourceNotFoundException e) {

            String errorMsg = messageSource.getMessage(e.getMessage(), null, LocaleContextHolder.getLocale());
            return ResponseEntity.status(404).body(errorMsg);

        } catch (RuntimeException e) {
            String errorMsg = messageSource.getMessage(e.getMessage(), null, LocaleContextHolder.getLocale());
            return ResponseEntity.badRequest().body(errorMsg);
        }
    }

    // 7. DELETE - XÓA USER

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(createResponse("user.delete.success", null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi hệ thống: " + e.getMessage());
        }
    }
}