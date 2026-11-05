package lab2.tickets;

import java.io.Serializable;
import java.time.LocalTime;

public class RegularTicket extends Ticket implements Editable, Serializable {
    public RegularTicket(int cardNumber, int office, String name, int priority, LocalTime time) {
        super(cardNumber, office, name, priority, time);
    }
    public RegularTicket(){}
}
