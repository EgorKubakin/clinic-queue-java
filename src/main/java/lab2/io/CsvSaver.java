package lab2.io;

import lab2.heap.BinHeap;
import lab2.tickets.Ticket;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CsvSaver {
    public static void save(BinHeap<Ticket> tickets, Path path) throws IOException {
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path)) {
            int size = tickets.getSize();
            for (int i = 0; i < size; i++) {
                String res = parseTicket(tickets.get(i));
                bufferedWriter.write(res);
                bufferedWriter.newLine();
            }
        }
    }

    private static String parseTicket(Ticket ticket) {
        for (CsvTicketHandler handler : CsvTicketHandler.values()) {
            if (handler.support(ticket)) {
                return handler.getString(ticket);
            }
        }
        throw new IllegalArgumentException(
                "Unsupported ticket type: " + ticket.getClass().getSimpleName()
        );
    }
}
