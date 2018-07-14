package com.example.searchengine.tasks;

import com.example.searchengine.dto.SearchingResultDto;
import com.example.searchengine.util.HtmlUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UrlSearchingTask extends AbstractSearchingTask {

    private BlockingQueue<SearchingResultDto> results;
    private String startUrl;
    private int countOfUrlsToScan;
    private SimpMessagingTemplate template;

    public UrlSearchingTask(BlockingQueue<SearchingResultDto> results, String startUrl, int countOfUrlsToScan, SimpMessagingTemplate template) {
        this.results = results;
        this.startUrl = startUrl;
        this.countOfUrlsToScan = countOfUrlsToScan;
        this.template = template;
    }

    @Override
    public void run() {
        try {
            searchByBreadth(startUrl);
            results.put(createResultForUrl(EXIT));
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted");
        }
    }

    private void searchByBreadth(String fromUrl) throws InterruptedException {
        List<String> unvisitedUrls = new LinkedList<>();
        unvisitedUrls.add(fromUrl);
        putResultIntoQueue(fromUrl);

        while (countOfUrlsToScan != 0 && !unvisitedUrls.isEmpty()) {

            synchronized (this) {
                while (suspended) {
                    System.out.println("paused");
                    wait();
                }
            }

            unvisitedUrls = unvisitedUrls
                    .stream()
                    .map(this::getUrls)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

            for (String unvisitedUrl : unvisitedUrls) {
                if (countOfUrlsToScan == 0) {
                    break;
                }
                putResultIntoQueue(unvisitedUrl);
            }
        }
    }

    private void putResultIntoQueue(String url) throws InterruptedException {
        SearchingResultDto result = createResultForUrl(url);
        results.put(result);
        template.convertAndSend("/topic/results", result);
        countOfUrlsToScan--;
    }

    private List<String> getUrls(String url) {
        try {
            return HtmlUtils.getUrlsFromPage(url);
        } catch (IOException ignored) {
        }
        return new ArrayList<>();
    }

    private SearchingResultDto createResultForUrl(String url) {
        SearchingResultDto result = new SearchingResultDto();
        result.setUrl(url);
        return result;
    }
}
