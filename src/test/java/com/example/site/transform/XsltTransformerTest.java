package com.example.site.transform;

import com.example.site.exception.SiteException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathFactory;
import java.net.URISyntaxException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class XsltTransformerTest {

    // Трансформер, який тестуємо
    private final XsltTransformer transformer = new XsltTransformer();

    // Повертає шлях до файлу з classpath
    private Path resource(String name) throws URISyntaxException {
        return Path.of(getClass().getResource(name).toURI());
    }

    // Обчислює XPath-вираз над документом і повертає результат як рядок
    private String xpath(Document document, String expression) throws Exception {
        return XPathFactory.newInstance().newXPath().evaluate(expression, document);
    }

    // Читає згенерований файл у DOM для перевірок
    private Document read(Path file) throws Exception {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file.toFile());
    }

    // Результат містить 4 групи за типами, і жодна сторінка не загублена
    @Test
    void transform_referenceFile_groupsPagesByType(@TempDir Path tempDir) throws Exception {
        Path out = tempDir.resolve("grouped.xml");

        transformer.transform(resource("/xml/site.xml"), resource("/xslt/group-by-type.xsl"), out);
        Document result = read(out);

        assertEquals("4", xpath(result, "count(/GroupedSite/Group)"));
        assertEquals("6", xpath(result, "count(//Page)"));
    }

    // Кожна група має очікувану кількість сторінок свого типу
    @Test
    void transform_referenceFile_putsPagesIntoCorrectGroups(@TempDir Path tempDir) throws Exception {
        Path out = tempDir.resolve("grouped.xml");

        transformer.transform(resource("/xml/site.xml"), resource("/xslt/group-by-type.xsl"), out);
        Document result = read(out);

        assertEquals("1", xpath(result, "count(/GroupedSite/Group[@type='ADVERTISING']/Page)"));
        assertEquals("2", xpath(result, "count(/GroupedSite/Group[@type='NEWS']/Page)"));
        assertEquals("2", xpath(result, "count(/GroupedSite/Group[@type='PORTAL']/Page)"));
        assertEquals("1", xpath(result, "count(/GroupedSite/Group[@type='MIRROR']/Page)"));
    }

    // Неіснуюча таблиця стилів дає SiteException
    @Test
    void transform_missingStylesheet_throwsSiteException(@TempDir Path tempDir) throws Exception {
        Path xml = resource("/xml/site.xml");
        Path out = tempDir.resolve("grouped.xml");

        assertThrows(SiteException.class,
                () -> transformer.transform(xml, Path.of("no-such.xsl"), out));
    }

    // Неіснуючий вхідний XML дає SiteException
    @Test
    void transform_missingXml_throwsSiteException(@TempDir Path tempDir) throws Exception {
        Path xsl = resource("/xslt/group-by-type.xsl");
        Path out = tempDir.resolve("grouped.xml");

        assertThrows(SiteException.class,
                () -> transformer.transform(Path.of("no-such.xml"), xsl, out));
    }
}