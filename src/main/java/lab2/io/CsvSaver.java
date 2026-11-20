package lab2.io;

import lab2.heap.BinHeap;
import lab2.tickets.HomeTicket;
import lab2.tickets.Ticket;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CsvSaver {
    public static void save(BinHeap<Ticket> tickets, Path path,int version) throws IOException {
        for (int i = 0; i < tickets.getSize(); i++) {
            Ticket ticket = tickets.get(i);
            checkCsvText(ticket.getName(), "Name");
            if (ticket instanceof HomeTicket home) {
                checkCsvText(home.getAdres(), "Address");
            }
            if (version >= 2) {
                checkCsvText(ticket.getComment(), "Comment");
            }
        }
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path)) {
            int size = tickets.getSize();
            bufferedWriter.write("#schema_version="+version);
            bufferedWriter.newLine();
            for (int i = 0; i < size; i++) {
                Ticket ticket = tickets.get(i);
                String res = parseTicket(ticket,version);
                bufferedWriter.write(res);
                bufferedWriter.newLine();
            }
        }
    }

    private static String parseTicket(Ticket ticket,int version) {
        for (CsvTicketHandler handler : CsvTicketHandler.values()) {
            if (handler.support(ticket)) {
                return handler.getString(ticket,version);
            }
        }
        throw new IllegalArgumentException(
                "Unsupported ticket type: " + ticket.getClass().getSimpleName()
        );
    }
    private static void checkCsvText(String value, String fieldName) {
        if (value != null && (value.contains(";") || value.contains("\n") || value.contains("\r"))) {
            throw new IllegalArgumentException(fieldName + " contains invalid CSV characters");
        }
    }
}
