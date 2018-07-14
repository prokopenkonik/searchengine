package com.example.searchengine.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class HtmlUtils {

    private static final String URL_REGEX =
            "(https?://)([a-zA-Z0-9\\-._~:/?#\\[\\]@!$&'()*+,;=%]+)";

    private HtmlUtils() {
    }

    public static boolean pageContentContainsWord(String url, String word)
            throws IOException {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                new URL(url).openStream()))) {
            return word != null && br
                    .lines()
                    .map(String::toLowerCase)
                    .anyMatch(s -> s.contains(word.toLowerCase()));
        }
    }

    public static List<String> getUrlsFromPage(String url) throws IOException {
        Pattern pattern = Pattern.compile(URL_REGEX);
        List<String> result = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new URL(url).openStream()))) {
            String text = br.lines().collect(Collectors.joining("\n"));
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                result.add(matcher.group());
            }
            return result;
        }
    }
}
