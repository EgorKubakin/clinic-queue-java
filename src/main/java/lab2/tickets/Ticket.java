package lab2.tickets;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lab2.io.serializable.binary.VersionedObjectInputStream;
import lab2.io.serializable.binary.VersionedObjectOutputStream;

import java.io.*;
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
    private transient String comment="";
    private transient boolean confirmed;
    @Serial
    private static final long serialVersionUID=3521485221538833199L;

    public Ticket(int cardNumber, int office, String name, int priority, LocalTime time, String comment, boolean confirmed) {
        this.cardNumber = cardNumber;
        this.office = office;
        this.name = name;
        setPriority(priority);
        this.time = time;
        this.comment = comment;
        this.confirmed = confirmed;
    }
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        long version;
        if(out instanceof VersionedObjectOutputStream ot){
            version= ot.getTargetVersion();
        }else {
            throw new IOException("Incorrect stream");
        }
        if(version == 3){
            out.writeObject(comment);
            out.writeBoolean(confirmed);
        }else if(version==2){
            out.writeObject(comment);
        }
    }
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        long version;
        if(in instanceof VersionedObjectInputStream ot){
            version= ot.getTargetVersion();
        }else {
            throw new IOException("Incorrect stream");
        }
        comment = version >= 2 ? (String) in.readObject() : "None";
        confirmed = version >= 3 && in.readBoolean();
    }
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public boolean isConfirmed() {
        return confirmed;
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
