package com.example.site.transform;

import com.example.site.exception.SiteException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

// Застосовує XSLT-таблицю стилів до XML-файлу й записує результат у файл
public class XsltTransformer {

    // Перетворює xmlPath за правилами xslPath і зберігає результат у outPath
    public void transform(Path xmlPath, Path xslPath, Path outPath) throws SiteException {
        try {
            // Створюємо каталог для результату, якщо його ще немає
            Files.createDirectories(outPath.toAbsolutePath().getParent());

            TransformerFactory factory = TransformerFactory.newInstance();
            // Компілюємо таблицю стилів у трансформер
            Transformer transformer = factory.newTransformer(new StreamSource(xslPath.toFile()));

            // Потік відкриваємо самі, щоб гарантовано закрити файл (важливо для Windows)
            try (OutputStream out = Files.newOutputStream(outPath)) {
                transformer.transform(new StreamSource(xmlPath.toFile()), new StreamResult(out));
            }
        } catch (TransformerException | IOException e) {
            throw new SiteException("XSLT transformation failed for " + xmlPath, e);
        }
    }
}