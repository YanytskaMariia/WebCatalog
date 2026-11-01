package com.example.site.model;

public record Chars(
        boolean email,
        boolean news,
        boolean archives,
        VotingType voting,
        DownloadType downloads
) {

}