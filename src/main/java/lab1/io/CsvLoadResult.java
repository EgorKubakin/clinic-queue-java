package lab1.io;
import lab1.tickets.Ticket;
import lab1.exception.CsvException;
import java.util.List;

public class CsvLoadResult {
    private final List<Ticket> tickets;
    private final List<CsvException> errors;


    public CsvLoadResult(List<Ticket> tickets, List<CsvException> errors) {
        this.tickets = tickets;
        this.errors = errors;
    }
    public List<Ticket> getTickets() {
        return tickets;
    }
    public List<CsvException> getErrors() {
        return errors;
    }
    public void adderr(CsvException e){
        errors.add(e);
    }
    public void addticket(Ticket e){
        tickets.add(e);
    }



}
