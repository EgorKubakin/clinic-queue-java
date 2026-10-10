package lab2.io;

import lab2.exception.CsvException;
import lab2.heap.BinHeap;
import lab2.tickets.Ticket;
import lab2.tickets.TicketType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class CsvLoader {
    public static CsvLoadResult load(Path path, Path errorLogPath) throws IOException {
        return load(path, errorLogPath, null);
    }

    public static CsvLoadResult load(Path path, Path errorLogPath, ProgressListener progress) throws IOException {
        long totalLines;
        try (Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8)) {
            totalLines = lines.count();
        }
        BinHeap<Ticket> tickets = new BinHeap<>((ticket1, ticket2) -> {
            int res = Integer.compare(ticket1.getPriority(), ticket2.getPriority());
            if (res == 0) {
                res = ticket1.getTime().compareTo(ticket2.getTime());
            }
            return res;
        });
        int errorCount = 0;
        try (BufferedWriter writer = Files.newBufferedWriter(errorLogPath, StandardCharsets.UTF_8)) {
            try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
                String str;
                int linenumber = 0;
                if (progress != null) {
                    progress.update(linenumber, totalLines);
                }
                while ((str = bufferedReader.readLine()) != null) {
                    linenumber++;
                    if (progress != null) {
                        if (progress.update(linenumber, totalLines)) {
                            break;
                        }
                        ;
                    }
                    try {
                        Ticket ticket = parseLine(str, linenumber);
                        if (ticket != null) {
                            tickets.add(ticket);
                        }
                    } catch (CsvException e) {
                        errorCount++;
                        writer.write("Line " + e.getLineNumber() + " -> " + e.getCode() + " : " + e.getMessage());
                        writer.newLine();
                    }
                }

            }
        }
        tickets.clearTrace();
        return new CsvLoadResult(tickets, errorCount);
    }

    private static Ticket parseLine(String line, int linenumber) throws CsvException {
        String[] args = line.split(";", -1);
        if (line.isBlank()) {
            throw new CsvException("ticket is null", CsvException.CsvErrorCode.EMPTY_FIELD, linenumber);
        }
        TicketType type;
        try {
            type = TicketType.valueOf(args[0]);
        } catch (IllegalArgumentException e) {
            throw new CsvException("unknown type", CsvException.CsvErrorCode.UNKNOWN_TYPE, linenumber);
        }
        CsvTicketHandler handler = null;
        for (CsvTicketHandler tp : CsvTicketHandler.values()) {
            if (type == tp.getType()) {
                handler = tp;
                break;
            }
        }
        if (handler == null) {
            throw new CsvException("unknown type", CsvException.CsvErrorCode.UNKNOWN_TYPE, linenumber);
        } else if (args.length != handler.getLen()) {
            throw new CsvException("incorrect field count ", CsvException.CsvErrorCode.WRONG_FIELD_COUNT, linenumber);
        }
        int cardNumber;
        int office;
        String name;
        int priority;
        LocalTime time;
        try {
            cardNumber = Integer.parseInt(args[1]);
            office = Integer.parseInt(args[3]);
            name = args[2];
            if (name.isBlank()) {
                throw new CsvException("Field name is null", CsvException.CsvErrorCode.EMPTY_FIELD, linenumber);
            }
            priority = Integer.parseInt(args[4]);
            time = LocalTime.parse(args[5]);
            if (priority < 0 || priority > 3) {
                throw new CsvException("Incorrect priority", CsvException.CsvErrorCode.INVALID_PRIORITY, linenumber);
            }
        } catch (NumberFormatException e) {
            throw new CsvException("Incorrect number", CsvException.CsvErrorCode.BAD_NUMBER, linenumber);
        } catch (DateTimeParseException e) {
            throw new CsvException("Incorrect time", CsvException.CsvErrorCode.BAD_NUMBER, linenumber);
        }
        return handler.create(cardNumber, office, name, priority, time, args, linenumber);

    }
}
