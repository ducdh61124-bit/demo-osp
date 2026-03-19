package com.example.demo.service;

import com.example.demo.entity.History;
import com.example.demo.repository.HistoryRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepository historyRepository;

    // 1. Hàm cho Frontend gọi để hiển thị lên bảng
    public List<History> getAllHistory() {
        return historyRepository.findAllByOrderByTimestampDesc();
    }

    // 2. Hàm dùng nội bộ Backend để lưu lịch sử
    public void logAction(String action, String entityType, String entityName, String fallbackUser, String details) {
        String actualUser = fallbackUser;
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String headerUser = request.getHeader("X-Username");
                if (headerUser != null && !headerUser.isEmpty()) {
                    actualUser = headerUser;
                }
            }
        } catch (Exception e) {
        }

        History history = History.builder()
                .action(action)
                .entityType(entityType)
                .entityName(entityName)
                .performedBy(actualUser)
                .timestamp(LocalDateTime.now())
                .details(details)
                .build();

        historyRepository.save(history);
    }
}

