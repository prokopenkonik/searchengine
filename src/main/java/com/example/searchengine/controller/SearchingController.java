package com.example.searchengine.controller;

import com.example.searchengine.dto.SearchingInfoDto;
import com.example.searchengine.service.SearchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SearchingController {

    private final SearchingService searchingService;
    private final SimpMessagingTemplate template;

    @Autowired
    public SearchingController(SearchingService searchingService, SimpMessagingTemplate template) {
        this.searchingService = searchingService;
        this.template = template;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @MessageMapping("/search")
    @SendTo("/topic/results")
    public void start(SearchingInfoDto searchingInfoDto) {
        searchingService.processSearching(searchingInfoDto, template);
    }

    @MessageMapping("/stop")
    public void stop() {
        searchingService.stopSearching();
    }

    @MessageMapping("/pause")
    public void pause() {
        searchingService.suspendSearching();
    }

    @MessageMapping("/resume")
    public void resume() {
        searchingService.resumeSearching();
    }
}
