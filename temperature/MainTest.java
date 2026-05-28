package temperature;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

class MainTest {

    private String run(List<String> rows, String prefix) throws IOException {
        Path in = Files.createTempFile(prefix, ".csv");
        Path out = Path.of(in + "_summary.txt");

        try {
            Files.write(in, rows);
            temperature.Main.processBatch(in.toString());
            return Files.readString(out);
        } finally {
            Files.deleteIfExists(in);
            Files.deleteIfExists(out);
        }
    }

    @Test
    void processBatch_generatesSummaryForValidData() throws IOException {
        String c = run(
            List.of("09:15:30,23.5", "09:16:00,24.1", "09:16:30,22.8", "09:17:00,25.3"),
            "temps-valid-"
        );

        assertTrue(c.contains("Total readings: 4"));
        assertTrue(c.contains("Valid readings: 4"));
        assertTrue(c.contains("Errors: 0"));
        assertTrue(c.contains("Max temperature: 25.30"));
        assertTrue(c.contains("Min temperature: 22.80"));
        assertTrue(c.contains("Average temperature: 23.93"));
    }

    @Test
    void processBatch_reportsInvalidRowsInSummary() throws IOException {
        String c = run(
            List.of("09:15:30,23.5", "bad-line", "09:16:00,999", "09:17:00,24.0"),
            "temps-mixed-"
        );

        assertTrue(c.contains("Total readings: 4"));
        assertTrue(c.contains("Valid readings: 2"));
        assertTrue(c.contains("Errors: 2"));
        assertTrue(c.contains("Max temperature: 24.00"));
        assertTrue(c.contains("Min temperature: 23.50"));
        assertTrue(c.contains("Average temperature: 23.75"));
    }
}
