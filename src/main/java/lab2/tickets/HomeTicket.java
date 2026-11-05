package lab2.tickets;

import java.io.Serializable;
import java.time.LocalTime;

public class HomeTicket extends Ticket implements Editable, Serializable {
    private String adres;

    public String getAdres() {
        return adres;
    }

    public void setAdres(String adres) {
        this.adres = adres;
    }

    public HomeTicket(int cardNumber, int office, String name, int priority, LocalTime time, String adres) {
        super(cardNumber, office, name, priority, time);
        this.adres = adres;
    }

    public HomeTicket(){}
}
