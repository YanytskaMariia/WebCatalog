package com.example.site.model;

public record Page(
        String id,
        String title,
        PageType type,
        Chars chars,
        boolean authorize
) {
}
