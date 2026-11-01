package com.example.site.parser;

import com.example.site.model.Chars;
import com.example.site.model.DownloadType;
import com.example.site.model.Page;
import com.example.site.model.PageType;
import com.example.site.model.VotingType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PageBuilderTest {

    // Тестуємо створення сторінки з повністю заповненими полями.
    // Перевіряємо шляхом порівняння згенерованого Page із заздалегідь створеним еталонним об'єктом.
    @Test
    void build_withAllFields_returnsExpectedPage() {
        PageBuilder builder = new PageBuilder()
                .withField(XmlTags.ATTR_ID, "p2")
                .withField(XmlTags.TITLE, "Daily News")
                .withField(XmlTags.TYPE, "NEWS")
                .withField(XmlTags.EMAIL, "true")
                .withField(XmlTags.NEWS, "true")
                .withField(XmlTags.ARCHIVES, "false")
                .withField(XmlTags.VOTING, "ANONYMOUS")
                .withField(XmlTags.DOWNLOADS, "FREE")
                .withField(XmlTags.AUTHORIZE, "false");

        Chars expectedChars = new Chars(true, true, false, VotingType.ANONYMOUS, DownloadType.FREE);
        Page expectedPage = new Page("p2", "Daily News", PageType.NEWS, expectedChars, false);

        Page actualPage = builder.build();
        assertEquals(expectedPage, actualPage);
    }

    // Тестуємо обробку відсутніх необов'язкових тегів.
    // Перевіряємо, що для невказаних прапорців у Chars встановлюється значення false за замовчуванням.
    @Test
    void build_withMissingOptionalFields_defaultsToFalse() {
        PageBuilder builder = new PageBuilder()
                .withField(XmlTags.ATTR_ID, "p1")
                .withField(XmlTags.TITLE, "Mega Sale Banner")
                .withField(XmlTags.TYPE, "ADVERTISING")
                .withField(XmlTags.VOTING, "NONE")
                .withField(XmlTags.DOWNLOADS, "FREE")
                .withField(XmlTags.AUTHORIZE, "false");

        Page actualPage = builder.build();

        assertFalse(actualPage.chars().email());
        assertFalse(actualPage.chars().news());
        assertFalse(actualPage.chars().archives());
    }

    // Тестуємо очищення вхідних текстових даних від пробілів та переносів рядків.
    // Перевіряємо, що після парсингу значення полів відповідають обрізаним рядкам та enum-типам.
    @Test
    void build_withWhitespaceAndNewlines_trimsValues() {
        PageBuilder builder = new PageBuilder()
                .withField(XmlTags.ATTR_ID, "  p2 \t")
                .withField(XmlTags.TITLE, "  Daily News \n")
                .withField(XmlTags.TYPE, " NEWS ")
                .withField(XmlTags.VOTING, " ANONYMOUS\n ")
                .withField(XmlTags.DOWNLOADS, " FREE ")
                .withField(XmlTags.AUTHORIZE, " false ");

        Page actualPage = builder.build();

        assertEquals("p2", actualPage.id());
        assertEquals("Daily News", actualPage.title());
        assertEquals(PageType.NEWS, actualPage.type());
    }

    // Тестуємо реакцію на невідоме значення enum-типу.
    // Перевіряємо, що передача значення, якого немає в PageType, викидає IllegalArgumentException.
    @Test
    void withField_invalidType_throwsIllegalArgumentException() {
        PageBuilder builder = new PageBuilder();

        assertThrows(IllegalArgumentException.class, () ->
                builder.withField(XmlTags.TYPE, "BLOG")
        );
    }
}