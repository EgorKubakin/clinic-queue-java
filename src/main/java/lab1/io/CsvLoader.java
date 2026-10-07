package lab1.io;

import lab1.exception.CsvException;
import lab1.tickets.Ticket;
import lab1.tickets.TicketType;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class CsvLoader {
    public static CsvLoadResult load(Path path) throws IOException {
        List<Ticket> tickets = new ArrayList<>();
        ArrayList<CsvException> errors = new ArrayList<>();
        CsvLoadResult res = new CsvLoadResult(tickets, errors);
        try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
            String str;
            int linenumber = 0;
            while ((str = bufferedReader.readLine()) != null) {
                try {
                    linenumber++;
                    Ticket ticket = parseLine(str, linenumber);
                    res.addticket(ticket);
                } catch (CsvException e) {
                    res.adderr(e);
                }
            }
        }
        return res;
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
        CsvTicketHandler handler=null;
        for (CsvTicketHandler tp : CsvTicketHandler.values()) {
            if (type==tp.getType()) {
                handler = tp;
                break;
            }
        }
        if (handler==null) {
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
