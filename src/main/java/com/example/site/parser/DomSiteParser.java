package com.example.site.parser;

import com.example.site.exception.SiteException;
import com.example.site.model.Page;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// Реалізація XML-парсера на основі DOM (Document Object Model)
public class DomSiteParser implements SiteParser {

    @Override
    public List<Page> parse(Path xmlPath) throws SiteException {
        // Завантажуємо весь XML у пам'ять у вигляді дерева
        Document document = loadDocument(xmlPath);
        // Знаходимо всі елементи Page у документі
        NodeList pageNodes = document.getElementsByTagName(XmlTags.PAGE);
        List<Page> pages = new ArrayList<>();

        try {
            for (int i = 0; i < pageNodes.getLength(); i++) {
                // Перетворюємо кожен елемент Page на об'єкт моделі
                pages.add(parsePage((Element) pageNodes.item(i)));
            }
        } catch (IllegalArgumentException e) {
            // Невідоме значення enum означає, що XML не відповідає схемі
            throw new SiteException("Invalid value in XML file: " + xmlPath, e);
        }

        return pages;
    }

    // Зчитує файл і створює DOM-документ
    private Document loadDocument(Path xmlPath) throws SiteException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlPath.toFile());
            // Об'єднуємо сусідні текстові вузли для коректної структури дерева
            document.getDocumentElement().normalize();
            return document;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new SiteException("Failed to parse XML file: " + xmlPath, e);
        }
    }

    // Перетворює один елемент Page на об'єкт Page через PageBuilder
    private Page parsePage(Element pageElement) {
        PageBuilder builder = new PageBuilder();
        // Атрибут id передаємо окремо, бо він не є дочірнім елементом
        builder.withField(XmlTags.ATTR_ID, pageElement.getAttribute(XmlTags.ATTR_ID));

        // Беремо всі вкладені елементи (Title, Type, Chars, Email ...) незалежно від рівня
        NodeList children = pageElement.getElementsByTagName("*");
        for (int i = 0; i < children.getLength(); i++) {
            Element child = (Element) children.item(i);
            // Builder сам ігнорує теги-обгортки, наприклад Chars
            builder.withField(child.getTagName(), child.getTextContent());
        }

        return builder.build();
    }
}