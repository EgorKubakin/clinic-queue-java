package lab2.generator;

import javafx.concurrent.Task;
import lab2.heap.BinHeap;
import lab2.tickets.Ticket;

public class TicketGeneratorTask extends Task<BinHeap<Ticket>> {
    private TicketGenerator generator;
    private BinHeap<Ticket> heap;
    private double ms;
    private int count;

    public TicketGeneratorTask(TicketGenerator generator, BinHeap<Ticket> heap, int count) {
        this.generator = generator;
        this.heap = heap;
        this.count = count;
    }

    public double getMs() {
        return ms;
    }

    @Override
    protected BinHeap<Ticket> call() throws Exception {
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be greater than 0");
        }
        updateProgress(0, count);
        int step = Math.max(1, count / 1000);
        long before = System.nanoTime();
        for (int i = 0; i < count; i++) {
            if (isCancelled()) {
                return null;
            }
            heap.add(generator.generateTicket(1000 + i));
            if (i % step == 0) {
                updateProgress(i + 1, count);
            }
        }

        updateProgress(count, count);
        long after = System.nanoTime();
        heap.clearTrace();
        ms = (after - before) / 1_000_000.0;
        return heap;
    }

}
