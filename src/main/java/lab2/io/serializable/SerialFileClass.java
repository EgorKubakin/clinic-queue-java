package lab2.io.serializable;
import lab2.tickets.Ticket;

import java.io.*;

public class SerialFileClass implements Serializable {
    @Serial
    private static final long serialVersionUID =-5800189672316899913L;
    private transient int schema_version;
    private Ticket[] tickets;

    public SerialFileClass(int version, Ticket[] tickets) {
        this.schema_version = version;
        this.tickets = tickets;
    }
    public SerialFileClass(){}

    public void setSchema_version(int schema_version) {
        this.schema_version = schema_version;
    }

    public Ticket[] getTickets() {
        return tickets;
    }

    public long getSchema_version() {
        return schema_version;
    }

    public void setTickets(Ticket[] tickets) {
        this.tickets = tickets;
    }
}
