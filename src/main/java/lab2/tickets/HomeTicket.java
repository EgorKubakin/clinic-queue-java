package lab2.tickets;

import lab2.io.serializable.SerialFileClass;

import java.io.ObjectStreamClass;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalTime;

public class HomeTicket extends Ticket implements Editable, Serializable {
    @Serial
    private static final long serialVersionUID=-6876341721255144116L;
    private String adres;

    public String getAdres() {
        return adres;
    }

    public void setAdres(String adres) {
        this.adres = adres;
    }

    public HomeTicket(int cardNumber, int office, String name, int priority, LocalTime time, String adres,String comment,boolean confirmed) {
        super(cardNumber, office, name, priority, time, comment, confirmed);
        this.adres = adres;
    }

    public HomeTicket(){}
}
