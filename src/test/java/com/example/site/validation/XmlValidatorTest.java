package com.example.site.validation;

import com.example.site.exception.SiteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URISyntaxException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class XmlValidatorTest {

    // Валідатор із реальною схемою проєкту
    private XmlValidator validator;

    // Перед кожним тестом завантажуємо схему з resources
    @BeforeEach
    void setUp() throws Exception {
        validator = new XmlValidator(resource("/xsd/site.xsd"));
    }

    // Повертає шлях до файлу з classpath
    private Path resource(String name) throws URISyntaxException {
        return Path.of(getClass().getResource(name).toURI());
    }

    // Еталонний файл проєкту має проходити валідацію
    @Test
    void validate_validFile_doesNotThrow() throws Exception {
        Path xml = resource("/xml/site.xml");

        assertDoesNotThrow(() -> validator.validate(xml));
    }

    // Файли, що порушують схему (неіснуючий Type, дубль id), мають давати SiteException
    @ParameterizedTest
    @ValueSource(strings = {"/invalid-site.xml", "/duplicate-id.xml"})
    void validate_invalidFile_throwsSiteException(String resourceName) throws Exception {
        Path xml = resource(resourceName);

        assertThrows(SiteException.class, () -> validator.validate(xml));
    }

    // Неіснуючий файл теж має давати SiteException, а не сирий виняток
    @Test
    void validate_missingFile_throwsSiteException() {
        assertThrows(SiteException.class, () -> validator.validate(Path.of("no-such-file.xml")));
    }

    // Неіснуюча схема має давати SiteException під час створення валідатора
    @Test
    void constructor_missingSchema_throwsSiteException() {
        assertThrows(SiteException.class, () -> new XmlValidator(Path.of("no-such-schema.xsd")));
    }
}