package lab2.experiment;

import lab2.generator.TicketGenerator;
import lab2.heap.BinHeap;
import lab2.tickets.Ticket;

import java.io.IOException;

public class HeapConcurrencyDemo {
    private static final int THREADS = 4;
    private static final int COUNT_TICKETS = 25_000;
    private static final Object LOCK = new Object();

    public static void main(String[] args) throws Exception {

        System.out.println("Without Synchronization");
        test(false);

        System.out.println();

        System.out.println("with Synchronization");
        test(true);
    }

    private static void test(boolean synchronizedMode) throws Exception {
        BinHeap<Ticket> heap = createHeap();
        Thread[] threads = new Thread[THREADS];
        for (int i = 0; i < THREADS; i++) {
            int threadNumber = i;
            threads[i] = new Thread(() -> {
                try {
                    TicketGenerator generator = new TicketGenerator();
                    for (int j = 0; j < COUNT_TICKETS; j++) {
                        Ticket ticket = generator.generateTicket(threadNumber * COUNT_TICKETS + j);
                        if (synchronizedMode) {
                            synchronized (LOCK) {
                                heap.add(ticket);
                            }
                        } else {
                            heap.add(ticket);
                        }
                    }
                } catch (IOException | RuntimeException e) {
                    System.out.println(Thread.currentThread().getName() + " failed: " + e.getClass().getSimpleName());
                }
            });
        }

        long start = System.nanoTime();
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        long end = System.nanoTime();
        int expected = THREADS * COUNT_TICKETS;
        System.out.println("Expected size: " + expected);
        System.out.println("Actual size:   " + heap.getSize());
        System.out.println("Time: " + (end - start) / 1_000_000.0 + " ms");
    }

    private static BinHeap<Ticket> createHeap() {

        return new BinHeap<>((ticket1, ticket2) -> {

            int result = Integer.compare(ticket1.getPriority(), ticket2.getPriority());
            if (result == 0) {
                result = ticket1.getTime().compareTo(ticket2.getTime());
            }
            return result;
        });
    }
}