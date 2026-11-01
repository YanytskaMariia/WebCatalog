package com.example.site.util;

import com.example.site.model.Chars;
import com.example.site.model.DownloadType;
import com.example.site.model.Page;
import com.example.site.model.PageType;
import com.example.site.model.VotingType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PageComparatorsTest {

    // Створює сторінку з потрібними id, назвою та типом
    private Page page(String id, String title, PageType type) {
        Chars chars = new Chars(false, false, false, VotingType.NONE, DownloadType.FREE);
        return new Page(id, title, type, chars, false);
    }

    // Сортує копію списку компаратором і повертає id в отриманому порядку
    private List<String> sortedIds(List<Page> pages, Comparator<Page> comparator) {
        List<Page> copy = new ArrayList<>(pages);
        copy.sort(comparator);
        return copy.stream().map(Page::id).toList();
    }

    // Спільні вхідні дані: id, назва, тип
    private final List<Page> pages = List.of(
            page("p1", "Beta", PageType.PORTAL),
            page("p2", "Alpha", PageType.PORTAL),
            page("p3", "Gamma", PageType.NEWS));

    // Сортування за назвою дає порядок Alpha, Beta, Gamma
    @Test
    void byTitle_sortsAlphabetically() {
        assertEquals(List.of("p2", "p1", "p3"), sortedIds(pages, PageComparators.BY_TITLE));
    }

    // Регістр не впливає на порядок: apple стоїть перед Banana
    @Test
    void byTitle_ignoresCase() {
        List<Page> mixed = List.of(
                page("p1", "Banana", PageType.NEWS),
                page("p2", "apple", PageType.NEWS));

        assertEquals(List.of("p2", "p1"), sortedIds(mixed, PageComparators.BY_TITLE));
    }

    // Сортування за типом: NEWS перед PORTAL, а сторінки одного типу лишаються у вихідному порядку
    @Test
    void byType_sortsByEnumOrderAndIsStable() {
        assertEquals(List.of("p3", "p1", "p2"), sortedIds(pages, PageComparators.BY_TYPE));
    }

    // Сортування за типом, а потім за назвою
    @Test
    void byTypeThenTitle_sortsByTypeAndThenByTitle() {
        assertEquals(List.of("p3", "p2", "p1"), sortedIds(pages, PageComparators.BY_TYPE_THEN_TITLE));
    }

    // Порожній список сортується без помилок
    @Test
    void sort_emptyList_returnsEmptyList() {
        assertEquals(List.of(), sortedIds(List.of(), PageComparators.BY_TITLE));
    }
}