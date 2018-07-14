package com.example.searchengine.service.impl;

import com.example.searchengine.dto.SearchingInfoDto;
import com.example.searchengine.dto.SearchingResultDto;
import com.example.searchengine.service.SearchingService;
import com.example.searchengine.tasks.AbstractSearchingTask;
import com.example.searchengine.tasks.UrlSearchingTask;
import com.example.searchengine.tasks.WordSearchingTask;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class SearchingServiceImpl implements SearchingService {

    private ExecutorService service;
    private List<AbstractSearchingTask> tasks;

    @Override
    public void processSearching(SearchingInfoDto searchingInfoDto, SimpMessagingTemplate template) {
        BlockingQueue<SearchingResultDto> results = new ArrayBlockingQueue<>(searchingInfoDto.getScannedUrlsCount() + 1);
        service = Executors.newFixedThreadPool(searchingInfoDto.getThreadsCount() + 1);

        tasks = new ArrayList<>();
        tasks.add(new UrlSearchingTask(
                results,
                searchingInfoDto.getUrl(),
                searchingInfoDto.getScannedUrlsCount(),
                template));
        for (int i = 0; i < searchingInfoDto.getThreadsCount(); i++) {
            tasks.add(new WordSearchingTask(
                    results,
                    searchingInfoDto.getTextToSearch(),
                    template));
        }
        tasks.forEach(service::submit);
        service.shutdown();
    }

    @Override
    public void stopSearching() {
        if (service != null) {
            service.shutdownNow();
        }
    }

    @Override
    public void suspendSearching() {
        tasks.forEach(AbstractSearchingTask::suspend);
    }

    @Override
    public void resumeSearching() {
        tasks.forEach(AbstractSearchingTask::resume);
    }
}
