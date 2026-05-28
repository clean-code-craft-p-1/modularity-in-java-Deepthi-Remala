package temperature;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Main {
    public static void processBatch(String filename) throws IOException {
        List<Double> temps = new ArrayList<>();
        int errors = 0, lines=0;
        for (String line : Files.readAllLines(Paths.get(filename))){
            lines++;
            line = line.trim();
            String[] p = line.split(",");
            if (p.length != 2 || p[0].strip().split(":").length != 3) {
                errors++;
                continue;
            }
            try {
                double t = Double.parseDouble(p[1].strip());
                if (t < -100 || t > 200) {
                    errors++;
                    continue;
                }
                temps.add(t);
            } catch (NumberFormatException e) {
                errors++;
            }
        }
        double max = Collections.max(temps);
        double min = Collections.min(temps);
        double avg = temps.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        System.out.println(String.format("Total readings: %d\nValid readings: %d\nErrors: %d\nMax temperature: %.2f\nMin temperature: %.2f\nAverage temperature: %.2f", lines, temps.size(), errors, max, min, avg));
        String outName = filename + "_summary.txt";
        try (PrintWriter out = new PrintWriter(new FileWriter(outName))) {
            out.println(String.format("Temperature Analysis Summary\n" + "==================================================\n" + "File analyzed: %s\n" + "Total readings: %d\n" + "Valid readings: %d\n" + "Errors: %d\n" + "Max temperature: %.2f\n" + "Min temperature: %.2f\n" + "Average temperature: %.2f\n" + "------------------------------------------------------------", filename, lines, temps.size(), errors, max, min, avg));
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java temperature.Main <input-csv-file>");
            return;
        }
        processBatch(args[0]);
    }
}