package temperature;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

class MainTest {

    @Test
    void processBatch_generatesSummaryForValidData() throws IOException {
        Path inputFile = Files.createTempFile("temps-valid-", ".csv");
        Path summaryFile = Path.of(inputFile + "_summary.txt");

        try {
            Files.write(inputFile, List.of(
                "09:15:30,23.5",
                "09:16:00,24.1",
                "09:16:30,22.8",
                "09:17:00,25.3"
            ));

            Main.processBatch(inputFile.toString());

            assertTrue(Files.exists(summaryFile), "Summary file should be created");
            String content = Files.readString(summaryFile);
            assertTrue(content.contains("Total readings: 4"));
            assertTrue(content.contains("Valid readings: 4"));
            assertTrue(content.contains("Errors: 0"));
        } finally {
            Files.deleteIfExists(inputFile);
            Files.deleteIfExists(summaryFile);
        }
    }

    @Test
    void processBatch_reportsInvalidRowsInSummary() throws IOException {
        Path inputFile = Files.createTempFile("temps-mixed-", ".csv");
        Path summaryFile = Path.of(inputFile + "_summary.txt");

        try {
            Files.write(inputFile, List.of(
                "09:15:30,23.5",
                "bad-line",
                "09:16:00,999",
                "09:17:00,24.0"
            ));

            Main.processBatch(inputFile.toString());

            String content = Files.readString(summaryFile);
            assertTrue(content.contains("Total readings: 4"));
            assertTrue(content.contains("Valid readings: 2"));
            assertTrue(content.contains("Errors: 2"));
            assertTrue(content.contains("Invalid lines:"));
            assertTrue(content.contains("Line 2: bad-line"));
            assertTrue(content.contains("Line 3: 09:16:00,999"));
            assertFalse(content.contains("No valid temperature data found."));
        } finally {
            Files.deleteIfExists(inputFile);
            Files.deleteIfExists(summaryFile);
        }
    }
}

