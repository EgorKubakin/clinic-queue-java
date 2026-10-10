package lab2.io;

import lab2.exception.CsvException;
import lab2.tickets.*;

import java.time.LocalTime;

public enum CsvTicketHandler {

    REGULAR(6, TicketType.REGULAR) {
        @Override
        Ticket createTicket(int cardNumber, int office, String name, int priority, LocalTime time, String address) {
            return new RegularTicket(cardNumber, office, name, priority, time);
        }

        @Override
        boolean support(Ticket ticket) {
            return ticket instanceof RegularTicket;
        }

        @Override
        String getString(Ticket ticket) {
            return CsvTicketHandler.getLine(getType(), ticket);
        }
    },
    CLOSED(6, TicketType.CLOSED) {
        @Override
        Ticket createTicket(int cardNumber, int office, String name, int priority, LocalTime time, String address) {
            return new CloseTicket(cardNumber, office, name, priority, time);
        }

        @Override
        boolean support(Ticket ticket) {
            return ticket instanceof CloseTicket;
        }

        @Override
        String getString(Ticket ticket) {
            return CsvTicketHandler.getLine(getType(), ticket);
        }
    },
    HOME(7, TicketType.HOME) {
        @Override
        Ticket createTicket(int cardNumber, int office, String name, int priority, LocalTime time, String address) {
            if (address == null || address.isBlank()) {
                throw new IllegalArgumentException("Address cannot be empty");
            }

            return new HomeTicket(cardNumber, office, name, priority, time, address
            );
        }

        @Override
        String getAddress(String[] args, int lineNumber) throws CsvException {
            String address = args[6];
            if (address.isBlank()) {
                throw new CsvException("Address is empty", CsvException.CsvErrorCode.EMPTY_FIELD, lineNumber);
            }
            return address;
        }

        @Override
        boolean support(Ticket ticket) {
            return ticket instanceof HomeTicket;
        }

        @Override
        String getString(Ticket ticket) {
            String address = ((HomeTicket) ticket).getAdres();
            return CsvTicketHandler.getLine(getType(), ticket) + ";" + address;
        }
    };

    private final int len;
    private final TicketType type;

    CsvTicketHandler(int len, TicketType type) {
        this.len = len;
        this.type = type;
    }

    public int getLen() {
        return len;
    }

    public TicketType getType() {
        return type;
    }

    public Ticket create(int cardNumber, int office, String name, int priority, LocalTime time, String[] args, int lineNumber) throws CsvException {
        String address = getAddress(args, lineNumber);
        return createTicket(cardNumber, office, name, priority, time, address);
    }

    public Ticket create(int cardNumber, int office, String name, int priority, LocalTime time, String address) {
        return createTicket(cardNumber, office, name, priority, time, address);
    }

    String getAddress(String[] args, int lineNumber) throws CsvException {
        return null;
    }

    abstract Ticket createTicket(int cardNumber, int office, String name, int priority, LocalTime time, String address);

    abstract String getString(Ticket ticket);

    abstract boolean support(Ticket ticket);

    public static CsvTicketHandler fromType(TicketType type) {
        for (CsvTicketHandler handler : values()) {
            if (handler.getType() == type) {
                return handler;
            }
        }
        throw new IllegalArgumentException("Unsupported ticket type: " + type);
    }

    private static String getLine(TicketType type, Ticket ticket) {
        return String.format("%s;%d;%s;%d;%d;%s", type.name(), ticket.getCardNumber(), ticket.getName(), ticket.getOffice(), ticket.getPriority(), ticket.getTime()
        );
    }
}