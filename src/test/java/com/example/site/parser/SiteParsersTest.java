package com.example.site.parser;

import com.example.site.exception.SiteException;
import com.example.site.model.Chars;
import com.example.site.model.DownloadType;
import com.example.site.model.Page;
import com.example.site.model.PageType;
import com.example.site.model.VotingType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SiteParsersTest {

    // Усі парсери, які проходять однакові перевірки
    static Stream<SiteParser> parsers() {
        return Stream.of(new DomSiteParser(), new SaxSiteParser(), new StaxSiteParser());
    }

    // Повертає шлях до еталонного site.xml із resources
    private Path siteXml() throws URISyntaxException {
        return Path.of(getClass().getResource("/xml/site.xml").toURI());
    }

    // Перевіряємо, що з еталонного файлу зчитано всі 6 сторінок
    @ParameterizedTest
    @MethodSource("parsers")
    void parse_validFile_returnsSixPages(SiteParser parser) throws Exception {
        List<Page> pages = parser.parse(siteXml());

        assertEquals(6, pages.size());
    }

    // Перевіряємо вміст першої (без необов'язкових полів) і п'ятої (Email=false) сторінок
    @ParameterizedTest
    @MethodSource("parsers")
    void parse_validFile_returnsExpectedPages(SiteParser parser) throws Exception {
        List<Page> pages = parser.parse(siteXml());

        Page expectedFirst = new Page("p1", "Mega Sale Banner", PageType.ADVERTISING,
                new Chars(false, false, false, VotingType.NONE, DownloadType.FREE), false);
        Page expectedFifth = new Page("p5", "Tech Weekly", PageType.NEWS,
                new Chars(false, true, false, VotingType.AUTHORIZED, DownloadType.FREE), true);

        assertEquals(expectedFirst, pages.get(0));
        assertEquals(expectedFifth, pages.get(4));
    }

    // Перевіряємо, що неіснуючий файл дає SiteException, а не сирий IOException
    @ParameterizedTest
    @MethodSource("parsers")
    void parse_missingFile_throwsSiteException(SiteParser parser) {
        assertThrows(SiteException.class, () -> parser.parse(Path.of("no-such-file.xml")));
    }

    // Перевіряємо, що всі парсери повертають однаковий список сторінок
    @Test
    void allParsers_returnSameResult() throws Exception {
        List<SiteParser> all = parsers().toList();
        List<Page> expected = all.get(0).parse(siteXml());

        for (SiteParser parser : all) {
            assertEquals(expected, parser.parse(siteXml()));
        }
    }
}