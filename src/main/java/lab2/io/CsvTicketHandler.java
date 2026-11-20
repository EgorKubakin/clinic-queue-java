package lab2.io;

import lab2.exception.CsvException;
import lab2.tickets.*;

import java.time.LocalTime;

public enum CsvTicketHandler {

    REGULAR(TicketType.REGULAR) {
        @Override
        Ticket createTicket(int cardNumber, int office, String name, int priority, LocalTime time, String address,String comment,boolean confirmed) {
            return new RegularTicket(cardNumber, office, name, priority, time,comment,confirmed);
        }

        @Override
        boolean support(Ticket ticket) {
            return ticket instanceof RegularTicket;
        }

        @Override
        String getString(Ticket ticket,int version) {
            return CsvTicketHandler.getLine(getType(), ticket,version);
        }
        @Override
        int getLen(int version) throws CsvException {
            switch (version){
                case 1-> {
                    return 6;
                }
                case 2-> {
                    return 7;
                }
                case 3-> {
                    return 8;
                }
                default -> throw new CsvException("Incorrect version", CsvException.CsvErrorCode.UNSUPPORTED_VERSION, 0);
            }
        }
    },
    CLOSED(TicketType.CLOSED) {
        @Override
        Ticket createTicket(int cardNumber, int office, String name, int priority, LocalTime time, String address,String comment,boolean confirmed) {
            return new CloseTicket(cardNumber, office, name, priority, time,comment,confirmed);
        }

        @Override
        boolean support(Ticket ticket) {
            return ticket instanceof CloseTicket;
        }
        @Override
        int getLen(int version) throws CsvException {
            switch (version){
                case 1-> {
                    return 6;
                }
                case 2-> {
                    return 7;
                }
                case 3-> {
                    return 8;
                }
                default -> throw new CsvException("Incorrect version", CsvException.CsvErrorCode.UNSUPPORTED_VERSION, 0);
            }
        }
        @Override
        String getString(Ticket ticket,int version) {
            return CsvTicketHandler.getLine(getType(), ticket,version);
        }
    },
    HOME( TicketType.HOME) {
        @Override
        Ticket createTicket(int cardNumber, int office, String name, int priority, LocalTime time, String address,String comment,boolean confirmed) {
            if (address == null || address.isBlank()) {
                throw new IllegalArgumentException("Address cannot be empty");
            }

            return new HomeTicket(cardNumber, office, name, priority, time, address,comment,confirmed);
        }
        @Override
        int getLen(int version) throws CsvException {
            switch (version){
                case 1-> {
                    return 7;
                }
                case 2-> {
                    return 8;
                }
                case 3-> {
                    return 9;
                }
                default -> throw new CsvException("Incorrect version", CsvException.CsvErrorCode.UNSUPPORTED_VERSION, 0);
            }
        }
        @Override
        String getAddress(String[] args, int lineNumber,int version) throws CsvException {
            String address;
            switch (version) {
                case 1 -> address = args[6];
                case 2 -> address = args[7];
                case 3 -> address = args[8];
                default ->   throw new CsvException("Incorrect version", CsvException.CsvErrorCode.UNSUPPORTED_VERSION, lineNumber);
            }
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
        String getString(Ticket ticket,int version) {
            String address = ((HomeTicket) ticket).getAdres();
            return CsvTicketHandler.getLine(getType(), ticket,version) + ";" + address;
        }
    };

    private final TicketType type;

    CsvTicketHandler( TicketType type) {

        this.type = type;
    }

    abstract int getLen(int version) throws CsvException;

    public TicketType getType() {
        return type;
    }

    public Ticket create(int cardNumber, int office, String name, int priority, LocalTime time, String[] args, int lineNumber,String comment,boolean confirmed,int version) throws CsvException {
        String address = getAddress(args, lineNumber,version);
        return createTicket(cardNumber, office, name, priority, time, address,comment,confirmed);
    }

    public Ticket create(int cardNumber, int office, String name, int priority, LocalTime time, String address,String comment,boolean confirmed) {
        return createTicket(cardNumber, office, name, priority, time, address,comment,confirmed);
    }

    String getAddress(String[] args, int lineNumber,int version) throws CsvException {
        return null;
    }

    abstract Ticket createTicket(int cardNumber, int office, String name, int priority, LocalTime time, String address,String comment,boolean confirmed);

    abstract String getString(Ticket ticket,int version);
    abstract boolean support(Ticket ticket);

    public static CsvTicketHandler fromType(TicketType type) {
        for (CsvTicketHandler handler : values()) {
            if (handler.getType() == type) {
                return handler;
            }
        }
        throw new IllegalArgumentException("Unsupported ticket type: " + type);
    }

    private static String getLine(TicketType type, Ticket ticket,int version) {
        String res= String.format("%s;%d;%s;%d;%d;%s", type.name(), ticket.getCardNumber(), ticket.getName(), ticket.getOffice(), ticket.getPriority(), ticket.getTime());
        if (version == 2) {
            res += ";" + ticket.getComment();
        } else if (version == 3) {
            res += ";" + ticket.getComment() + ";" + ticket.isConfirmed();
        }
        return res;
    }
}