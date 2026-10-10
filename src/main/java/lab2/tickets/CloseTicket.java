package lab2.tickets;

import java.time.LocalTime;

public class CloseTicket extends Ticket {
    public CloseTicket(int cardNumber, int office, String name, int priority, LocalTime time) {
        super(cardNumber, office, name, priority, time);
    }
}
