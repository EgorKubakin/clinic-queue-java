package lab2.io.serializable;
import lab2.tickets.Ticket;

import java.io.Serializable;

public class SerialFileClass implements Serializable {
    private long version;
    private Ticket[] tickets;

    public SerialFileClass(long version, Ticket[] tickets) {
        this.version = version;
        this.tickets = tickets;
    }
    public SerialFileClass(){}

    public long getVersion() {
        return version;
    }

    public Ticket[] getTickets() {
        return tickets;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public void setTickets(Ticket[] tickets) {
        this.tickets = tickets;
    }
}
