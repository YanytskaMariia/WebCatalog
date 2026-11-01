package com.example.site.parser;

import com.example.site.exception.SiteException;
import com.example.site.model.Page;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

// Реалізація XML-парсера на основі SAX (послідовне читання подій)
public class SaxSiteParser implements SiteParser {

    @Override
    public List<Page> parse(Path xmlPath) throws SiteException {
        // Обробник, який передає події в колектор сторінок
        PageHandler handler = new PageHandler();

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            // Запускаємо читання файлу, події йдуть в обробник
            parser.parse(xmlPath.toFile(), handler);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new SiteException("Failed to parse XML file: " + xmlPath, e);
        } catch (IllegalArgumentException e) {
            // Невідоме значення enum означає, що XML не відповідає схемі
            throw new SiteException("Invalid value in XML file: " + xmlPath, e);
        }

        return handler.getPages();
    }

    // Обробник SAX-подій: лише передає їх у PageCollector
    private static class PageHandler extends DefaultHandler {

        // Спільна логіка збирання сторінок
        private final PageCollector collector = new PageCollector();

        List<Page> getPages() {
            return collector.getPages();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            // Для елементів без id метод getValue повертає null, і колектор його ігнорує
            collector.startElement(qName, attributes.getValue(XmlTags.ATTR_ID));
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            // Перетворюємо порцію символів на рядок і віддаємо колектору
            collector.addText(new String(ch, start, length));
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            collector.endElement(qName);
        }
    }
}