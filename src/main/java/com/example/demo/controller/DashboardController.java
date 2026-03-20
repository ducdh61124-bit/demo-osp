package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import com.example.demo.repository.*;

import java.util.*;
import java.util.stream.*;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final HistoryRepository historyRepository;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // 1. Số liệu tổng quan (Counters)
        stats.put("totalBooks", bookRepository.count());
        stats.put("totalCategories", categoryRepository.count());
        stats.put("totalUsers", userRepository.count());
        stats.put("totalLogs", historyRepository.count());

        // 2. Phân bố sách theo danh mục (Dữ liệu cho biểu đồ tròn)
        List<Map<String, Object>> categoryDist = categoryRepository.findAll().stream().map(cat -> {
            Map<String, Object> item = new HashMap<>();
            item.put("name", cat.getName());
            item.put("value", cat.getBooks().size());
            return item;
        }).collect(Collectors.toList());
        stats.put("categoryDistribution", categoryDist);

        // 3. Hoạt động gần đây (Lấy 5 dòng log mới nhất)
        stats.put("recentActivities", historyRepository.findAllByOrderByTimestampDesc().stream().limit(5).toList());

        return ResponseEntity.ok(stats);
    }
}
