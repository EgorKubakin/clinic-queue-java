package lab2.io.serializable;
import lab2.exception.SerialException;
import lab2.tickets.HomeTicket;
import lab2.tickets.Ticket;
public class TicketValidator {
    public static Ticket validate(Ticket ticket,int ticketNumber) throws SerialException {
        if(ticket==null){
            throw new SerialException("Ticket is null", SerialException.SerialErrCode.EMPTY_TICKET,ticketNumber);
        }
        if(ticket.getTime()==null||ticket.getName()==null||ticket.getName().isBlank()){
            throw new SerialException("Ticket contain empty fields", SerialException.SerialErrCode.EMPTY_FIELD,ticketNumber);
        }
        if(ticket.getPriority()<0||ticket.getPriority()>3||ticket.getOffice()<0||ticket.getCardNumber()<=0){
            throw new SerialException("Ticket contain incorrect fields", SerialException.SerialErrCode.INCORRECT_FIELD,ticketNumber);
        }
        if(ticket instanceof HomeTicket homeTicket){
            if(homeTicket.getAdres()==null||homeTicket.getAdres().isBlank()){
                throw new SerialException("Ticket contain empty fields", SerialException.SerialErrCode.EMPTY_FIELD,ticketNumber);
            }
        }
        return ticket;

    }
}
