package com.example.site.util;

import com.example.site.model.Page;

import java.util.Comparator;

// Набір готових компараторів для сортування сторінок
public final class PageComparators {

    // Сортування за назвою без урахування регістру
    public static final Comparator<Page> BY_TITLE =
            Comparator.comparing(Page::title, String.CASE_INSENSITIVE_ORDER);

    // Сортування за типом у порядку оголошення констант PageType
    public static final Comparator<Page> BY_TYPE =
            Comparator.comparing(Page::type);

    // Спочатку за типом, а сторінки одного типу впорядковані за назвою
    public static final Comparator<Page> BY_TYPE_THEN_TITLE =
            BY_TYPE.thenComparing(BY_TITLE);

    // Утилітний клас, екземпляри не створюються
    private PageComparators() {
    }
}