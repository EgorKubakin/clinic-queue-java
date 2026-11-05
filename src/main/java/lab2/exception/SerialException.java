
package lab2.exception;

import java.io.IOException;

public class SerialException extends IOException {

    public enum SerialErrCode {
        EMPTY_FIELD("Required field is empty"),
        INCORRECT_FIELD("Ticket contains invalid data"),
        EMPTY_TICKET("File contains an empty ticket"),
        FILE_IO("Failed to read or write the file"),
        UNKNOW_CLASS("Unknown ticket type"),
        CAST_CLASS("Invalid object type in file"),
        UNSUPPORTED_VERSION("Unsupported file version");

        private final String shortMessage;

        SerialErrCode(String shortMessage) {
            this.shortMessage = shortMessage;
        }

        public String getShortMessage() {
            return shortMessage;
        }
    }

    private int ticketindex = -1;
    private final SerialErrCode code;

    public SerialException(String message, SerialErrCode code, int ticketindex) {
        super(message);
        this.code = code;
        this.ticketindex = ticketindex;
    }

    public SerialException(String message, SerialErrCode code, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public SerialException(String message, SerialErrCode code) {
        super(message);
        this.code = code;
    }

    public SerialErrCode getCode() {
        return code;
    }

    public int getTicketindex() {
        return ticketindex;
    }

    public String getShortMessage() {
        return code.getShortMessage();
    }
}
