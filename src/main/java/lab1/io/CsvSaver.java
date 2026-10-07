package lab1.io;
import lab1.tickets.Ticket;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CsvSaver {
    public static void save(List<Ticket> tickets, Path path) throws IOException {
        try(BufferedWriter bufferedWriter= Files.newBufferedWriter(path)){
            for(Ticket ticket:tickets){
                String res = parseTicket(ticket);
                bufferedWriter.write(res);
                bufferedWriter.newLine();
            }
        }
    }
    private static String parseTicket(Ticket ticket) {
        for(CsvTicketHandler handler: CsvTicketHandler.values()){
            if(handler.support(ticket)){
                return handler.getString(ticket);
            }
        }
        throw new IllegalArgumentException(
                "Unsupported ticket type: " + ticket.getClass().getSimpleName()
        );
    }
}
