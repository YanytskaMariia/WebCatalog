package com.example.site.parser;

import com.example.site.exception.SiteException;
import com.example.site.model.Page;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

// Реалізація XML-парсера на основі StAX (читання подій за запитом)
public class StaxSiteParser implements SiteParser {

    @Override
    public List<Page> parse(Path xmlPath) throws SiteException {
        // Спільна логіка збирання сторінок
        PageCollector collector = new PageCollector();
        XMLInputFactory factory = XMLInputFactory.newInstance();

        // Потік відкриваємо окремо, бо reader.close() його не закриває
        try (InputStream in = Files.newInputStream(xmlPath)) {
            XMLStreamReader reader = factory.createXMLStreamReader(in);
            try {
                readEvents(reader, collector);
            } finally {
                reader.close();
            }
        } catch (XMLStreamException | IOException e) {
            throw new SiteException("Failed to parse XML file: " + xmlPath, e);
        } catch (IllegalArgumentException e) {
            // Невідоме значення enum означає, що XML не відповідає схемі
            throw new SiteException("Invalid value in XML file: " + xmlPath, e);
        }

        return collector.getPages();
    }

    // Крок за кроком читає події з потоку й передає їх колектору
    private void readEvents(XMLStreamReader reader, PageCollector collector) throws XMLStreamException {
        while (reader.hasNext()) {
            // next() переходить до наступної події й повертає її тип
            int event = reader.next();

            switch (event) {
                case XMLStreamConstants.START_ELEMENT ->
                    // Другий аргумент це значення атрибута id, для інших елементів воно null
                        collector.startElement(reader.getLocalName(), reader.getAttributeValue(null, XmlTags.ATTR_ID));
                case XMLStreamConstants.CHARACTERS ->
                        collector.addText(reader.getText());
                case XMLStreamConstants.END_ELEMENT ->
                        collector.endElement(reader.getLocalName());
                default -> {
                    // Решту подій (коментарі, початок документа) пропускаємо
                }
            }
        }
    }
}