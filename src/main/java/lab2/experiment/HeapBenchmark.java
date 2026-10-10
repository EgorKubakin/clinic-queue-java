package lab2.experiment;

import lab2.generator.TicketGenerator;
import lab2.heap.BinHeap;
import lab2.tickets.Ticket;

import java.io.IOException;


public class HeapBenchmark {

    public static void main(String[] args) {

        test(10_000);
        test(100_000);
    }

    private static void test(int size) {

        System.out.println("SIZE = " + size);
        try {
            BinHeap<Ticket> heap = generate(size);


            Ticket ticket = getTicket();
            long start = System.nanoTime();
            heap.add(ticket);
            long end = System.nanoTime();
            System.out.println("Add: " + (end - start) / 1_000_000.0 + " ms");


            Ticket ticket1 = heap.get(size - 1);
            start = System.nanoTime();
            heap.search(ticket1);
            end = System.nanoTime();
            System.out.println("Search: " + (end - start) / 1_000_000.0 + " ms");


            Ticket ticketRemove = heap.get(heap.getSize() - 1);
            start = System.nanoTime();
            heap.remove(ticketRemove);
            end = System.nanoTime();
            System.out.println("Remove: " + (end - start) / 1_000_000.0 + " ms");


            BinHeap<Ticket> first = generate(size / 2);
            BinHeap<Ticket> second = generate(size / 2);
            start = System.nanoTime();
            BinHeap<Ticket> merged = first.merge(second);
            end = System.nanoTime();
            System.out.println("Merge: " + (end - start) / 1_000_000.0 + " ms");

            Ticket ticketUrg = heap.get(size - 1);

            int newPriority;
            if (ticketUrg.getPriority() == 0) {
                newPriority = 3;
            } else {
                newPriority = 0;
            }
            start = System.nanoTime();
            int index = heap.search(ticketUrg);
            ticketUrg.setPriority(newPriority);
            heap.updatePosition(index);
            end = System.nanoTime();
            System.out.println("Change Priority: " + (end - start) / 1_000_000.0 + " ms");


            int k = 100;
            start = System.nanoTime();
            BinHeap<Ticket> urgentCopy = heap.copy();
            for (int i = 0; i < k; i++) {
                urgentCopy.pop();
            }
            end = System.nanoTime();
            System.out.println("Most Urgent for 100 tickets: " + (end - start) / 1_000_000.0 + " ms");

        } catch (IOException e) {
            e.printStackTrace();
        }

        // дальше Delete, K urgent и т.д.
    }

    private static BinHeap<Ticket> generate(int count) throws IOException {
        BinHeap<Ticket> heap = new BinHeap<>((ticket1, ticket2) -> {
            int res = Integer.compare(ticket1.getPriority(), ticket2.getPriority());
            if (res == 0) {
                res = ticket1.getTime().compareTo(ticket2.getTime());
            }
            return res;
        });
        TicketGenerator ticketGenerator = new TicketGenerator();
        ticketGenerator.generate(heap, count);
        return heap;
    }

    private static Ticket getTicket() throws IOException {
        TicketGenerator ticketGenerator = new TicketGenerator();
        return ticketGenerator.generateTicket(0);
    }
}
