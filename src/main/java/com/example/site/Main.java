package com.example.site;

import com.example.site.exception.SiteException;
import com.example.site.model.Page;
import com.example.site.parser.DomSiteParser;
import com.example.site.parser.SaxSiteParser;
import com.example.site.parser.SiteParser;
import com.example.site.parser.StaxSiteParser;
import com.example.site.transform.XsltTransformer;
import com.example.site.util.PageComparators;
import com.example.site.validation.PageRulesValidator;
import com.example.site.validation.XmlValidator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

// Точка входу: перевіряє XML, читає його трьома парсерами, сортує та групує через XSLT
public class Main {

    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    // Шляхи до вхідних файлів у resources
    private static final String XML_RESOURCE = "/xml/site.xml";
    private static final String XSD_RESOURCE = "/xsd/site.xsd";
    private static final String XSL_RESOURCE = "/xslt/group-by-type.xsl";

    // Файл, куди записується результат XSLT-перетворення
    private static final Path OUTPUT_FILE = Path.of("output", "grouped-site.xml");

    public static void main(String[] args) {
        configureLogging();

        try {
            run(OUTPUT_FILE);
        } catch (SiteException | URISyntaxException e) {
            // Єдине місце, де помилка потрапляє в лог разом зі стеком
            LOG.log(Level.SEVERE, "Application failed", e);
        }
    }

    // Виконує весь потік обробки, результат трансформації зберігає в outputFile
    static void run(Path outputFile) throws SiteException, URISyntaxException {
        Path xml = resourcePath(XML_RESOURCE);
        Path xsd = resourcePath(XSD_RESOURCE);
        Path xsl = resourcePath(XSL_RESOURCE);

        // Крок 1: перевірка XML за схемою
        new XmlValidator(xsd).validate(xml);
        LOG.info("XML is valid against the XSD schema");

        // Крок 2: читання трьома парсерами, колекція береться з першого
        List<Page> pages = parseWithAllParsers(xml);

        // Крок 3: перевірка правил, залежних від типу сторінки
        checkBusinessRules(pages);

        // Крок 4: сортування копії списку за типом, а потім за назвою
        List<Page> sorted = new ArrayList<>(pages);
        sorted.sort(PageComparators.BY_TYPE_THEN_TITLE);
        sorted.forEach(page -> LOG.info("Sorted: " + page.id() + " " + page.type() + " " + page.title()));

        // Крок 5: групування сторінок за типом через XSLT
        new XsltTransformer().transform(xml, xsl, outputFile);
        LOG.info("Grouped result saved to " + outputFile.toAbsolutePath());
    }

    // Читає файл усіма парсерами, логує результат кожного й повертає список першого
    private static List<Page> parseWithAllParsers(Path xml) throws SiteException {
        List<SiteParser> parsers = List.of(new DomSiteParser(), new SaxSiteParser(), new StaxSiteParser());
        List<Page> result = null;

        for (SiteParser parser : parsers) {
            List<Page> pages = parser.parse(xml);
            LOG.info(parser.getClass().getSimpleName() + " parsed " + pages.size() + " pages");
            if (result == null) {
                result = pages;
            }
        }

        return result;
    }

    // Записує попередження для кожного порушення бізнес-правил
    private static void checkBusinessRules(List<Page> pages) {
        PageRulesValidator validator = new PageRulesValidator();

        for (Page page : pages) {
            validator.validate(page).forEach(LOG::warning);
        }
    }

    // Повертає шлях до файлу з classpath
    private static Path resourcePath(String resource) throws URISyntaxException {
        return Path.of(Main.class.getResource(resource).toURI());
    }

    // Завантажує налаштування логування з logging.properties
    private static void configureLogging() {
        try (InputStream config = Main.class.getResourceAsStream("/logging.properties")) {
            // Каталог для файлу логів має існувати до створення FileHandler
            Files.createDirectories(Path.of("logs"));
            LogManager.getLogManager().readConfiguration(config);
        } catch (IOException e) {
            // Логер ще не налаштований, тому пишемо в стандартний потік помилок
            System.err.println("Could not configure logging: " + e.getMessage());
        }
    }
}