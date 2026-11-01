package com.example.site.validation;

import com.example.site.exception.SiteException;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.nio.file.Path;

// Перевіряє XML-файл на відповідність XSD-схемі
public class XmlValidator {

    // Скомпільована схема, завантажується один раз у конструкторі
    private final Schema schema;

    public XmlValidator(Path xsdPath) throws SiteException {
        try {
            // Фабрика, яка розуміє мову XML Schema
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            this.schema = factory.newSchema(xsdPath.toFile());
        } catch (SAXException e) {
            throw new SiteException("Failed to load XSD schema: " + xsdPath, e);
        }
    }

    // Кидає SiteException, якщо файл не відповідає схемі або його не вдалося прочитати
    public void validate(Path xmlPath) throws SiteException {
        try {
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xmlPath.toFile()));
        } catch (SAXException | IOException e) {
            // У повідомлення додаємо причину від парсера, там є рядок і опис помилки
            throw new SiteException("XML validation failed for " + xmlPath + ": " + e.getMessage(), e);
        }
    }
}