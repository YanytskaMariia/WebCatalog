package com.example.site.validation;

import com.example.site.model.Chars;
import com.example.site.model.Page;
import com.example.site.model.PageType;

import java.util.ArrayList;
import java.util.List;

// Перевіряє правила, які XSD 1.0 виразити не може: залежність характеристик від типу сторінки
public class PageRulesValidator {

    // Повертає список порушень для сторінки, порожній список означає, що все гаразд
    public List<String> validate(Page page) {
        List<String> violations = new ArrayList<>();
        Chars chars = page.chars();
        PageType type = page.type();

        // Пошта допустима для всіх типів, крім рекламних сторінок
        if (chars.email() && type == PageType.ADVERTISING) {
            violations.add(message(page, "Email is allowed only for PORTAL, MIRROR and NEWS"));
        }
        // Новини допустимі лише для сторінок новин
        if (chars.news() && type != PageType.NEWS) {
            violations.add(message(page, "News is allowed only for NEWS"));
        }
        // Архіви допустимі лише для дзеркал
        if (chars.archives() && type != PageType.MIRROR) {
            violations.add(message(page, "Archives is allowed only for MIRROR"));
        }

        return violations;
    }

    // Формує повідомлення з id сторінки, щоб порушення було легко знайти в XML
    private String message(Page page, String problem) {
        return "Page " + page.id() + ": " + problem;
    }
}