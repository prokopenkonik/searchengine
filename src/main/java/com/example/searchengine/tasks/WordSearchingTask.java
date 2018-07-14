package com.example.searchengine.tasks;

import com.example.searchengine.dto.SearchingResultDto;
import com.example.searchengine.util.HtmlUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class WordSearchingTask extends AbstractSearchingTask {

    private final BlockingQueue<SearchingResultDto> results;
    private final String textToSearch;
    private SimpMessagingTemplate template;

    public WordSearchingTask(BlockingQueue<SearchingResultDto> results,
                             String textToSearch,
                             SimpMessagingTemplate template) {
        this.results = results;
        this.textToSearch = textToSearch;
        this.template = template;
    }

    @Override
    public void run() {
        try {
            while (true) {

                synchronized (this) {
                    while (suspended) {
                        System.out.println("paused");
                        wait();
                    }
                }

                SearchingResultDto result = results.take();
                if (result.getUrl().equals(EXIT)) {
                    results.put(result);
                    break;
                }

                try {
                    result.setWordFound(
                            HtmlUtils.pageContentContainsWord(
                                    result.getUrl(), textToSearch));
                } catch (IOException e) {
                    result.setHasError(true);
                    if (e.getMessage().equals(result.getUrl())) {
                        result.setErrorInfo("Could not read a text from " + result.getUrl());
                    } else {
                        result.setErrorInfo(e.getMessage());
                    }
                }

                result.setInProgress(false);
                template.convertAndSend("/topic/results", result);
            }
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted");
        }
    }
}
