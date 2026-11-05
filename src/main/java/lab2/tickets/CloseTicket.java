package lab2.tickets;

import java.io.Serializable;
import java.time.LocalTime;

public class CloseTicket extends Ticket implements Serializable {
    public CloseTicket(int cardNumber, int office, String name, int priority, LocalTime time) {
        super(cardNumber, office, name, priority, time);
    }
    public CloseTicket(){}
}
