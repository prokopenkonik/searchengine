package com.example.searchengine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchingInfoDto {

    private String url;
    private int threadsCount;
    private String textToSearch;
    private int scannedUrlsCount;

}
