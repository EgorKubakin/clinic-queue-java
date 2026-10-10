package lab2.experiment;

import lab2.generator.TicketGenerator;
import lab2.heap.BinHeap;
import lab2.tickets.Ticket;

import java.io.IOException;
import java.util.Comparator;
import java.util.PriorityQueue;

public class HeapComparison {
    private static final Comparator<Ticket> comparator = (ticket1, ticket2) -> {
        int res = Integer.compare(
                ticket1.getPriority(),
                ticket2.getPriority()
        );

        if (res == 0) {
            res = ticket1.getTime().compareTo(ticket2.getTime());
        }

        return res;
    };

    public static void main(String[] args) throws IOException {

        test(10_000);
        test(100_000);
    }

    private static void test(int size) throws IOException {
        System.out.println("SIZE = " + size);
        Ticket[] tickets = generateTickets(size);
        BinHeap<Ticket> binHeap = new BinHeap<>(comparator);
        PriorityQueue<Ticket> priorityQueue = new PriorityQueue<>(comparator);
        long start = System.nanoTime();
        for (Ticket ticket : tickets) {
            binHeap.add(ticket);
        }
        long end = System.nanoTime();

        System.out.println("BinHeap add: " + (end - start) / 1_000_000.0 + " ms");
        start = System.nanoTime();
        for (Ticket ticket : tickets) {
            priorityQueue.add(ticket);
        }
        end = System.nanoTime();
        System.out.println("PriorityQueue add: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        while (binHeap.getSize() > 0) {
            binHeap.pop();
        }
        end = System.nanoTime();
        System.out.println("BinHeap pop: " + (end - start) / 1_000_000.0 + " ms");


        start = System.nanoTime();
        while (!priorityQueue.isEmpty()) {
            priorityQueue.poll();
        }
        end = System.nanoTime();
        System.out.println("PriorityQueue poll: " + (end - start) / 1_000_000.0 + " ms");
        System.out.println();
    }


    private static Ticket[] generateTickets(int size) throws IOException {

        Ticket[] tickets = new Ticket[size];

        TicketGenerator generator =
                new TicketGenerator();

        for (int i = 0; i < size; i++) {
            tickets[i] = generator.generateTicket(i);
        }

        return tickets;
    }
}
