package lab2.tickets;

import java.io.ObjectStreamClass;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalTime;

public class RegularTicket extends Ticket implements Editable, Serializable {
    @Serial
    private static final long serialVersionUID=3779697493122062675L;
    public RegularTicket(int cardNumber, int office, String name, int priority, LocalTime time,String comment,boolean confirmed) {
       super(cardNumber, office, name, priority, time, comment, confirmed);
    }
    public RegularTicket(){}
}
