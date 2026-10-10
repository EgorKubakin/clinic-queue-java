package lab2.generator;

import lab2.heap.BinHeap;
import lab2.io.CsvTicketHandler;
import lab2.tickets.Ticket;
import lab2.tickets.TicketType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.Random;

public class TicketGenerator {
    private String[] names;
    private String[] surnames;
    private String[] address;
    private double ms;
    private final Random random = new Random();

    public TicketGenerator() throws IOException {
        names = loadResources("names.txt");
        surnames = loadResources("surnames.txt");
        address = loadResources("addresses.txt");
    }

    public String[] loadResources(String fileName) throws IOException {
        InputStream input = TicketGenerator.class.getResourceAsStream(
                "/lab2/generator/" + fileName
        );
        if (input == null) {
            throw new IOException("Resource not found: " + fileName);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            return reader.lines().filter(line -> !line.isBlank()).toArray(String[]::new);
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }

    }

    public Ticket generateTicket(int cardNumber) {
        int office = random.nextInt(1, 100);
        int nameIndex = random.nextInt(0, names.length);
        int surIndex = random.nextInt(0, surnames.length);
        int priority = random.nextInt(0, 4);
        int hours = random.nextInt(8, 22);
        int minute = random.nextInt(0, 60);
        LocalTime time = LocalTime.of(hours, minute);
        int addressIndex = random.nextInt(0, address.length);
        TicketType type = TicketType.values()[random.nextInt(TicketType.values().length)];
        CsvTicketHandler handler = CsvTicketHandler.fromType(type);
        return handler.create(cardNumber, office, names[nameIndex] + " " + surnames[surIndex], priority, time, address[addressIndex]);
    }

    public double getMs() {
        return ms;
    }

    public void generate(BinHeap<Ticket> heap, int count) {
        if (heap == null || count <= 0) {
            throw new IllegalArgumentException("Incorrect argument");
        }
        long before = System.nanoTime();
        for (int i = 0; i < count; i++) {
            heap.add(generateTicket(1000 + i));
        }
        long after = System.nanoTime();
        ms = (after - before) / 1_000_000.0;
        heap.clearTrace();
    }
}
