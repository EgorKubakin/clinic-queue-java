package lab2.io;

import lab2.heap.BinHeap;
import lab2.tickets.Ticket;
import lab2.exception.CsvException;

import java.util.List;

public class CsvLoadResult {
    private BinHeap<Ticket> tickets;
    private int errorCount;

    public CsvLoadResult(BinHeap<Ticket> tickets, int errorCount) {
        this.tickets = tickets;
        this.errorCount = errorCount;
    }

    public BinHeap<Ticket> getTickets() {
        return tickets;
    }

    public int getErrorCount() {
        return errorCount;
    }
}
