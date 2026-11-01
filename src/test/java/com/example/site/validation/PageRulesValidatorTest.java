package com.example.site.validation;

import com.example.site.model.Chars;
import com.example.site.model.DownloadType;
import com.example.site.model.Page;
import com.example.site.model.PageType;
import com.example.site.model.VotingType;
import com.example.site.parser.DomSiteParser;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PageRulesValidatorTest {

    // Валідатор, який тестуємо
    private final PageRulesValidator validator = new PageRulesValidator();

    // Створює сторінку заданого типу з потрібними прапорцями Chars
    private Page page(PageType type, boolean email, boolean news, boolean archives) {
        Chars chars = new Chars(email, news, archives, VotingType.NONE, DownloadType.FREE);
        return new Page("p1", "Test", type, chars, false);
    }

    // Коректна сторінка новин не має порушень
    @Test
    void validate_validNewsPage_returnsNoViolations() {
        assertTrue(validator.validate(page(PageType.NEWS, true, true, false)).isEmpty());
    }

    // Рекламна сторінка з поштою порушує правило Email
    @Test
    void validate_advertisingWithEmail_returnsViolation() {
        List<String> violations = validator.validate(page(PageType.ADVERTISING, true, false, false));

        assertEquals(1, violations.size());
    }

    // Портал із новинами порушує правило News
    @Test
    void validate_portalWithNews_returnsViolation() {
        List<String> violations = validator.validate(page(PageType.PORTAL, false, true, false));

        assertEquals(1, violations.size());
    }

    // Портал з архівами порушує правило Archives
    @Test
    void validate_portalWithArchives_returnsViolation() {
        List<String> violations = validator.validate(page(PageType.PORTAL, false, false, true));

        assertEquals(1, violations.size());
    }

    // Кілька порушень одночасно мають давати кілька повідомлень
    @Test
    void validate_multipleViolations_returnsAllOfThem() {
        List<String> violations = validator.validate(page(PageType.ADVERTISING, true, true, true));

        assertEquals(3, violations.size());
    }

    // Усі сторінки еталонного site.xml мають відповідати правилам
    @Test
    void validate_referenceFile_hasNoViolations() throws Exception {
        Path xml = Path.of(getClass().getResource("/xml/site.xml").toURI());

        for (Page page : new DomSiteParser().parse(xml)) {
            assertTrue(validator.validate(page).isEmpty(), "Violations on page " + page.id());
        }
    }
}