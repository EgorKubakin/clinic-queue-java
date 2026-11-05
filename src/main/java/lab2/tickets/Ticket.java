package lab2.tickets;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;
import java.time.LocalTime;
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RegularTicket.class, name = "REGULAR"),
        @JsonSubTypes.Type(value = HomeTicket.class, name = "HOME"),
        @JsonSubTypes.Type(value = CloseTicket.class, name = "CLOSED")
})
public abstract class Ticket implements Serializable {
    private int cardNumber;
    private int office;
    private String name;
    private int priority;
    private LocalTime time;

    protected Ticket(int cardNumber, int office, String name, int priority, LocalTime time) {
        setCardNumber(cardNumber);
        setOffice(office);
        setName(name);
        setPriority(priority);
        setTime(time);
    }
    public Ticket(){}
    public void setCardNumber(int cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setOffice(int office) {
        this.office = office;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPriority(int priority) {
        if (priority < 0 || priority > 3) {
            throw new IllegalArgumentException("Priority must be from 0 to 3");
        }
        this.priority = priority;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public int getCardNumber() {
        return cardNumber;
    }

    public int getOffice() {
        return office;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public LocalTime getTime() {
        return time;
    }
}
