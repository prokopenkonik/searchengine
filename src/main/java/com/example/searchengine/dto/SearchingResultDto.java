package com.example.searchengine.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.UUID;

@Data
public class SearchingResultDto {

    @Setter(AccessLevel.NONE)
    private String id;
    private String url;
    private boolean inProgress;
    private boolean wordFound;
    private boolean hasError;
    private String errorInfo;

    public SearchingResultDto() {
        this.id = UUID.randomUUID().toString();
        inProgress = true;
    }
}
