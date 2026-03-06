package com.example.demo.controller;

import com.example.demo.configuration.BookstoreAppPropertiesConfiguration;
import com.example.demo.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system")
public class SystemController {

    @Autowired
    private BookstoreAppPropertiesConfiguration appProperties;

    @GetMapping("/info")
    public ResponseEntity<ApiResponse> getSystemInfo() {

        ApiResponse response = new ApiResponse(
                200,
                "Lấy thông tin hệ thống thành công",
                appProperties,
                null
        );

        return ResponseEntity.ok(response);
    }
}