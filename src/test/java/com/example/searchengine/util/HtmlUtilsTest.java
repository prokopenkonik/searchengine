package com.example.searchengine.util;

import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class HtmlUtilsTest {

    private static final String URL = "https://www.google.com/";
    private static final String WORD = "google";
    private static final List<String> URLS = Arrays.asList(
            "http://schema.org/WebPage",
            "https://drive.google.com/?tab=wo");

    @Test
    public void pageContentContainsWord() throws IOException {
        assertTrue(
                HtmlUtils.pageContentContainsWord(URL, WORD)
        );
        assertFalse(
                HtmlUtils.pageContentContainsWord(URL, null)
        );
    }

    @Test
    public void getUrlsFromPage() throws IOException {
        List<String> result = HtmlUtils.getUrlsFromPage(URL);
        assertTrue(result.containsAll(URLS));
    }
}