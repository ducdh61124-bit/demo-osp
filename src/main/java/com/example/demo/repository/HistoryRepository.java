package com.example.demo.repository;

import com.example.demo.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long>{
    List<History> findAllByOrderByTimestampDesc();
}
