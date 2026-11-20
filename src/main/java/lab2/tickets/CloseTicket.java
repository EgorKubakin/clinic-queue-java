package lab2.tickets;

import java.io.ObjectStreamClass;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalTime;

public class CloseTicket extends Ticket implements Serializable {
    @Serial
    private static final long serialVersionUID=-7517671916238693600L;
    public CloseTicket(int cardNumber, int office, String name, int priority, LocalTime time,String comment,boolean confirmed) {
        super(cardNumber, office, name, priority, time, comment, confirmed);
    }
    public CloseTicket(){}
}
