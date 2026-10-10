package lab2.exception;

public class CsvException extends Exception {
    public enum CsvErrorCode {
        UNKNOWN_TYPE,
        WRONG_FIELD_COUNT,
        BAD_NUMBER,
        EMPTY_FIELD,
        INVALID_PRIORITY
    }

    private final int lineNumber;
    private final CsvErrorCode code;

    public CsvException(String message, CsvErrorCode code, int lineNumber) {
        super(message);
        this.code = code;
        this.lineNumber = lineNumber;

    }

    public int getLineNumber() {
        return lineNumber;
    }

    public CsvErrorCode getCode() {
        return code;
    }
}

