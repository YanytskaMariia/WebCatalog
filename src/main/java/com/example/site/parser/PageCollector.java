package com.example.site.parser;

import com.example.site.model.Page;

import java.util.ArrayList;
import java.util.List;

// Збирає сторінки з подій "початок тегу / текст / кінець тегу" (спільна логіка SAX і StAX)
class PageCollector {

    // Готові сторінки, зібрані з документа
    private final List<Page> pages = new ArrayList<>();
    // Текст поточного елемента, який може приходити частинами
    private final StringBuilder text = new StringBuilder();
    // Builder поточної сторінки, поза Page дорівнює null
    private PageBuilder builder;

    // Викликається на початку кожного елемента, idValue потрібен лише для Page
    void startElement(String name, String idValue) {
        // Кожен елемент починається з порожнього буфера тексту
        text.setLength(0);

        if (XmlTags.PAGE.equals(name)) {
            // Починається нова сторінка, створюємо для неї builder
            builder = new PageBuilder();
            // Атрибут id передаємо одразу, бо він приходить разом з відкриваючим тегом
            builder.withField(XmlTags.ATTR_ID, idValue);
        }
    }

    // Додає порцію тексту, бо парсер може розбити текст елемента на кілька викликів
    void addText(String chunk) {
        text.append(chunk);
    }

    // Викликається в кінці кожного елемента, коли текст уже повний
    void endElement(String name) {
        if (XmlTags.PAGE.equals(name)) {
            // Сторінка закінчилась, збираємо об'єкт і додаємо до списку
            pages.add(builder.build());
            builder = null;
        } else if (builder != null) {
            // Елемент усередині Page, передаємо його текст у builder
            builder.withField(name, text.toString());
        }
    }

    // Повертає всі зібрані сторінки
    List<Page> getPages() {
        return pages;
    }
}