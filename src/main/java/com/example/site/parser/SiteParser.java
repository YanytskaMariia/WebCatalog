package com.example.site.parser;

import com.example.site.exception.SiteException;
import com.example.site.model.Page;

import java.nio.file.Path;
import java.util.List;

// Спільний інтерфейс для всіх трьох парсерів (SAX, DOM, StAX)
public interface SiteParser {

    // Приймає шлях до XML-файлу, парсить його та повертає список сторінок Page
    List<Page> parse(Path xmlPath) throws SiteException;
}