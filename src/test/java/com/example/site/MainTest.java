package com.example.site;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    // Весь потік на еталонних даних має завершитись без помилок і створити файл результату
    @Test
    void run_referenceData_createsGroupedFile(@TempDir Path tempDir) {
        Path output = tempDir.resolve("grouped-site.xml");

        assertDoesNotThrow(() -> Main.run(output));

        assertTrue(Files.exists(output));
    }
}