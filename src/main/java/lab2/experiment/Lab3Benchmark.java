
package lab2.experiment;

import lab2.generator.TicketGenerator;
import lab2.heap.BinHeap;
import lab2.io.serializable.*;
import lab2.io.serializable.binary.BinSerializable;
import lab2.io.serializable.json.JsonSerializer;
import lab2.tickets.Ticket;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class Lab3Benchmark {

    public static void main(String[] args) throws Exception {

        Comparator<Ticket> comparator = (a, b) -> {
            int result = Integer.compare(a.getPriority(), b.getPriority());
            if (result == 0) {
                result = a.getTime().compareTo(b.getTime());
            }
            return result;
        };

        BinHeap<Ticket> heap = new BinHeap<>(comparator);
        new TicketGenerator().generate(heap, 10_000);

        Path folder = Path.of("build", "benchmark");
        Files.createDirectories(folder);

        test("JSON", new JsonSerializer(), folder.resolve("tickets.json"), heap, comparator);
        test("BIN", new BinSerializable(), folder.resolve("tickets.bin"), heap, comparator);
    }

    private static void test(String name, Serializer serializer, Path path,
                             BinHeap<Ticket> heap, Comparator<Ticket> comparator) throws Exception {

        double saveTime = 0;
        double loadTime = 0;

        for (int i = 0; i < 5; i++) {

            long start = System.nanoTime();
            serializer.save(path, heap, 1);
            saveTime += (System.nanoTime() - start) / 1_000_000.0;

            start = System.nanoTime();
            BinHeap<Ticket> loaded = serializer.load(path, comparator);
            loadTime += (System.nanoTime() - start) / 1_000_000.0;

            if (loaded.getSize() != heap.getSize()) {
                throw new IllegalStateException("Incorrect loaded size");
            }
        }

        System.out.println("\nFormat: " + name);
        System.out.printf("File size: %.2f KB%n", Files.size(path) / 1024.0);
        System.out.printf("Average save: %.3f ms%n", saveTime / 5);
        System.out.printf("Average load: %.3f ms%n", loadTime / 5);
    }
}
