package lab1.io;

import lab1.exception.CsvException;
import lab1.tickets.*;

import java.time.LocalTime;


public enum CsvTicketHandler {
    REGULAR(6,TicketType.REGULAR){

        @Override
        Ticket create(int cardNumber, int office, String name, int priority, LocalTime time, String[] args, int lineNumber) throws CsvException {
            return new RegularTicket(cardNumber, office, name, priority, time);
        }
        @Override
        boolean support(Ticket ticket){
            return ticket instanceof RegularTicket;
        }
        @Override
        public String getString(Ticket ticket) {
            return CsvTicketHandler.getLine(getType(), ticket);
        }
    },
    CLOSED(6,TicketType.CLOSED){
        @Override
        Ticket create(int cardNumber, int office, String name, int priority, LocalTime time, String[] args, int lineNumber) throws CsvException {
            return new CloseTicket(cardNumber, office, name, priority, time);
        }
        @Override
        boolean support(Ticket ticket){
            return ticket instanceof CloseTicket;
        }
        @Override
        public String getString(Ticket ticket) {
            return CsvTicketHandler.getLine(getType(),ticket);
        }
    },
    HOME(7,TicketType.HOME){

        @Override
        Ticket create(int cardNumber, int office, String name, int priority, LocalTime time, String[] args, int lineNumber) throws CsvException{
            String adres;
            if ((adres = args[6]).isBlank()) {
                throw new CsvException("ticket is null", CsvException.CsvErrorCode.EMPTY_FIELD, lineNumber);
            }
            return new HomeTicket(cardNumber,office,name,priority,time,adres);
        }
        @Override
        boolean support(Ticket ticket){
            return ticket instanceof HomeTicket;
        }
        @Override
        public String getString(Ticket ticket) {
            String address = ((HomeTicket) ticket).getAdres();
            return CsvTicketHandler.getLine(getType(),ticket)+";"+address;
        }
    };
    private final int len;
    private final TicketType type;
    private CsvTicketHandler(int len, TicketType type){
        this.len = len;
        this.type=type;

    }

    public TicketType getType() {
        return type;
    }

    public int getLen(){
        return len;
    }

    private static String getLine(TicketType type, Ticket ticket) {
        return String.format("%s;%d;%s;%d;%d;%s", type.name(), ticket.getCardNumber(), ticket.getName(), ticket.getOffice(), ticket.getPriority(), ticket.getTime());
    }
    abstract String getString(Ticket ticket);
    abstract boolean support(Ticket ticket);
    abstract Ticket create(int cardNumber, int office, String name, int priority, LocalTime time, String[] args, int lineNumber) throws CsvException;

}
