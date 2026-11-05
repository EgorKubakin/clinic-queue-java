package basicOperationtests;
import lab2.heap.BinHeap;
import lab2.io.CsvLoadResult;
import lab2.io.CsvLoader;
import lab2.io.CsvSaver;
import lab2.tickets.CloseTicket;
import lab2.tickets.HomeTicket;
import lab2.tickets.RegularTicket;
import lab2.tickets.Ticket;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;
public class BasicOperationTests {
    private Comparator<Ticket> comparator=(ticket1, ticket2) -> {
        int res = Integer.compare(ticket1.getPriority(), ticket2.getPriority());
        if (res == 0) {
            res = ticket1.getTime().compareTo(ticket2.getTime());
        }
        return res;
    };
    @Test
    public void add4ElementInHeap(){
        BinHeap<Ticket> heap = new BinHeap<>(comparator);
        Ticket ticket1 = new RegularTicket(120,12,"Egor",2, LocalTime.of(7,30));
        Ticket ticket2 = new HomeTicket(130,1,"Ivan",1,LocalTime.of(8,30),"New Orleans 33");
        Ticket ticket3 = new CloseTicket(200,15,"Bob",3, LocalTime.of(12,30));
        Ticket ticket4 = new RegularTicket(150,9,"Kate",0, LocalTime.of(9,30));
        heap.add(ticket1);
        heap.add(ticket2);
        heap.add(ticket3);
        heap.add(ticket4);
        assertEquals(4,heap.getSize());
        assertEquals(ticket4,heap.getMin());
    }
    @Test
    public void pop1ElementInHeap(){
        BinHeap<Ticket> heap = new BinHeap<>(comparator);
        Ticket ticket1 = new RegularTicket(120,12,"Egor",2, LocalTime.of(7,30));
        Ticket ticket2 = new HomeTicket(130,1,"Ivan",1,LocalTime.of(8,30),"New Orleans 33");
        Ticket ticket3 = new CloseTicket(200,15,"Bob",3, LocalTime.of(12,30));
        Ticket ticket4 = new RegularTicket(150,9,"Kate",0, LocalTime.of(9,30));
        heap.add(ticket1);
        heap.add(ticket2);
        heap.add(ticket3);
        heap.add(ticket4);
        Ticket ticket = heap.pop();
        assertEquals(3,heap.getSize());
        assertEquals(ticket,ticket4);
    }
    @Test
    public void CsvRound(@TempDir Path temp) throws IOException {
        BinHeap<Ticket> heap = new BinHeap<>(comparator);
        Ticket ticket1 = new RegularTicket(120,12,"Egor",2, LocalTime.of(7,30));
        Ticket ticket2 = new HomeTicket(130,1,"Ivan",1,LocalTime.of(8,30),"New Orleans 33");
        Ticket ticket3 = new CloseTicket(200,15,"Bob",3, LocalTime.of(12,30));
        Ticket ticket4 = new RegularTicket(150,9,"Kate",0, LocalTime.of(9,30));
        heap.add(ticket1);
        heap.add(ticket2);
        heap.add(ticket3);
        heap.add(ticket4);
        Path path = temp.resolve("temp.csv");
        Path path1 = temp.resolve("testerrorlog.csv");
        CsvSaver.save(heap,path);
        CsvLoadResult res = CsvLoader.load(path,path1);
        BinHeap<Ticket> heap1= res.getTickets();
        int expectedSize = 4;
        assertEquals(expectedSize,heap1.getSize());
        Ticket ticket4S = heap1.pop();
        Ticket ticket2S = heap1.pop();
        Ticket ticket1S = heap1.pop();
        Ticket ticket3S = heap1.pop();
        assertAll("Compare Ticket1",()->assertInstanceOf(RegularTicket.class,ticket1S),()->assertEquals(ticket1.getCardNumber(),ticket1S.getCardNumber()),()->assertEquals(ticket1.getOffice(),ticket1S.getOffice()),()->assertEquals(ticket1.getName(),ticket1S.getName()),()->assertEquals(ticket1.getPriority(),ticket1S.getPriority()),()->assertEquals(ticket1.getTime(),ticket1S.getTime()));
        assertAll("Compare Ticket2",()->assertInstanceOf(HomeTicket.class,ticket2S),()->assertEquals(ticket2.getCardNumber(),ticket2S.getCardNumber()),()->assertEquals(ticket2.getOffice(),ticket2S.getOffice()),()->assertEquals(ticket2.getName(),ticket2S.getName()),()->assertEquals(ticket2.getPriority(),ticket2S.getPriority()),()->assertEquals(ticket2.getTime(),ticket2S.getTime()),()->assertEquals(((HomeTicket) ticket2).getAdres(),((HomeTicket) ticket2S).getAdres()));
        assertAll("Compare Ticket3",()->assertInstanceOf(CloseTicket.class,ticket3S),()->assertEquals(ticket3.getCardNumber(),ticket3S.getCardNumber()),()->assertEquals(ticket3.getOffice(),ticket3S.getOffice()),()->assertEquals(ticket3.getName(),ticket3S.getName()),()->assertEquals(ticket3.getPriority(),ticket3S.getPriority()),()->assertEquals(ticket3.getTime(),ticket3S.getTime()));
        assertAll("Compare Ticket4",()->assertInstanceOf(RegularTicket.class,ticket4S),()->assertEquals(ticket4.getCardNumber(),ticket4S.getCardNumber()),()->assertEquals(ticket4.getOffice(),ticket4S.getOffice()),()->assertEquals(ticket4.getName(),ticket4S.getName()),()->assertEquals(ticket4.getPriority(),ticket4S.getPriority()),()->assertEquals(ticket4.getTime(),ticket4S.getTime()));
    }
}
