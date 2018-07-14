package com.example.searchengine.service;

import com.example.searchengine.dto.SearchingInfoDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public interface SearchingService {
    void processSearching(SearchingInfoDto searchingInfoDto, SimpMessagingTemplate template);
    void stopSearching();
    void suspendSearching();
    void resumeSearching();
}
