package lab1.io;
import lab1.tickets.CloseTicket;
import lab1.tickets.HomeTicket;
import lab1.tickets.RegularTicket;
import lab1.tickets.Ticket;
import lab1.tickets.TicketType;
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
        int CardName=ticket.getCardNumber();
        int office = ticket.getOffice();
        String name = ticket.getName();
        int priority = ticket.getPriority();
        String time = ticket.getTime().toString();
        if(ticket instanceof RegularTicket){
            return String.format("%s;%d;%s;%d;%d;%s",TicketType.REGULAR.name(),CardName,name,office,priority,time);
        } else if (ticket instanceof HomeTicket) {
            String adres = ((HomeTicket) ticket).getAdres();
            return String.format("%s;%d;%s;%d;%d;%s;%s",TicketType.HOME.name(),CardName,name,office,priority,time,adres);
        }else if(ticket instanceof CloseTicket){
            return String.format("%s;%d;%s;%d;%d;%s",TicketType.CLOSED.name(),CardName,name,office,priority,time);
        }
        throw new IllegalArgumentException(
                "Unsupported ticket type: " + ticket.getClass().getSimpleName()
        );
    }
}
