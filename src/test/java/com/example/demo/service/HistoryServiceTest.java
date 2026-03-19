package com.example.demo.service;

import com.example.demo.entity.History;
import com.example.demo.repository.HistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class HistoryServiceTest {
    @Mock private HistoryRepository historyRepository;
    @InjectMocks private HistoryService historyService;

    @Test void testGetAll_Success() { when(historyRepository.findAllByOrderByTimestampDesc()).thenReturn(List.of(new History())); assertNotNull(historyService.getAllHistory()); }
    @Test void testGetAll_Empty() { when(historyRepository.findAllByOrderByTimestampDesc()).thenReturn(Collections.emptyList()); assertTrue(historyService.getAllHistory().isEmpty()); }
    @Test void testLog_SaveCalled() { historyService.logAction("A", "B", "C", "D", "E"); verify(historyRepository).save(any()); }
    @Test void testLog_FieldsMapping() {
        ArgumentCaptor<History> captor = ArgumentCaptor.forClass(History.class);
        historyService.logAction("ACT", "TYPE", "NAME", "USER", "DET");
        verify(historyRepository).save(captor.capture());
        assertEquals("ACT", captor.getValue().getAction());
    }
    @Test void testLog_NullRequestAttributes() { historyService.logAction("A", "B", "C", "admin", "D"); verify(historyRepository).save(any()); } // Test catch block
    @Test void testLog_TimestampNotNull() {
        ArgumentCaptor<History> captor = ArgumentCaptor.forClass(History.class);
        historyService.logAction("A", "B", "C", "D", "E");
        verify(historyRepository).save(captor.capture());
        assertNotNull(captor.getValue().getTimestamp());
    }
    @Test void testLog_PerformedByFallback() {
        ArgumentCaptor<History> captor = ArgumentCaptor.forClass(History.class);
        historyService.logAction("A", "B", "C", "fallback", "D");
        verify(historyRepository).save(captor.capture());
        assertEquals("fallback", captor.getValue().getPerformedBy());
    }
    // (Các test bóc Header cần MockStatic RequestContextHolder - yêu cầu mockito-inline)
    @Test void testLog_ActionNotNull() { historyService.logAction("LOGIN", "AUTH", "U", "A", "D"); verify(historyRepository).save(argThat(h -> h.getAction().equals("LOGIN"))); }
    @Test void testLog_EntityTypeCorrect() { historyService.logAction("A", "BOOK", "C", "D", "E"); verify(historyRepository).save(argThat(h -> h.getEntityType().equals("BOOK"))); }
    @Test void testLog_MultipleSaves() { historyService.logAction("1","1","1","1","1"); historyService.logAction("2","2","2","2","2"); verify(historyRepository, times(2)).save(any()); }
}