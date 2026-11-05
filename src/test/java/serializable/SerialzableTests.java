package serializable;

import lab2.exception.SerialException;
import lab2.heap.BinHeap;
import lab2.io.serializable.BinSerializable;
import lab2.io.serializable.JsonSerializer;
import lab2.io.serializable.Serializer;
import lab2.tickets.CloseTicket;
import lab2.tickets.HomeTicket;
import lab2.tickets.RegularTicket;
import lab2.tickets.Ticket;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.Comparator;

public class SerialzableTests {
    private Comparator<Ticket> comparator=(ticket1, ticket2) -> {
        int res = Integer.compare(ticket1.getPriority(), ticket2.getPriority());
        if (res == 0) {
            res = ticket1.getTime().compareTo(ticket2.getTime());
        }
        return res;
    };

    @Test
    public void JsonRoundTrip4Elements(@TempDir Path temp) throws SerialException {
        BinHeap<Ticket> heap = new BinHeap<>(comparator);
        Ticket ticket1 = new RegularTicket(120,12,"Egor",2, LocalTime.of(7,30));
        Ticket ticket2 = new HomeTicket(130,1,"Ivan",1,LocalTime.of(8,30),"New Orleans 33");
        Ticket ticket3 = new CloseTicket(200,15,"Bob",3, LocalTime.of(12,30));
        Ticket ticket4 = new RegularTicket(150,9,"Kate",0, LocalTime.of(9,30));
        heap.add(ticket1);
        heap.add(ticket2);
        heap.add(ticket3);
        heap.add(ticket4);
        Path path = temp.resolve("JsonRound.json");
        Serializer json = new JsonSerializer();
        json.save(path,heap,1);
        BinHeap<Ticket> heap1= json.load(path,comparator,1);
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
    @Test
    public void BinRoundTrip4Elements(@TempDir Path temp) throws SerialException {
        BinHeap<Ticket> heap = new BinHeap<>(comparator);
        Ticket ticket1 = new RegularTicket(120,12,"Egor",2, LocalTime.of(7,30));
        Ticket ticket2 = new HomeTicket(130,1,"Ivan",1,LocalTime.of(8,30),"New Orleans 33");
        Ticket ticket3 = new CloseTicket(200,15,"Bob",3, LocalTime.of(12,30));
        Ticket ticket4 = new RegularTicket(150,9,"Kate",0, LocalTime.of(9,30));
        heap.add(ticket1);
        heap.add(ticket2);
        heap.add(ticket3);
        heap.add(ticket4);
        Path path = temp.resolve("BinRound.json");
        Serializer bin = new BinSerializable();
        bin.save(path,heap,1);
        BinHeap<Ticket> heap1= bin.load(path,comparator,1);
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
    @Test
    public void SerializeEmptyHeapJson(@TempDir Path temp) throws SerialException {
        BinHeap<Ticket> heap = new BinHeap<>(comparator);
        Serializer json = new JsonSerializer();
        Path path = temp.resolve("Empty.json");
        json.save(path,heap,1);
        BinHeap<Ticket> heap1 =  json.load(path,comparator,1);
        assertEquals(heap.getSize(),heap1.getSize());
    }
    @Test
    public void SerializeEmptyHeapBin(@TempDir Path temp) throws SerialException {
        BinHeap<Ticket> heap = new BinHeap<>(comparator);
        Serializer json = new BinSerializable();
        Path path = temp.resolve("Empty.bin");
        json.save(path,heap,1);
        BinHeap<Ticket> heap1 =  json.load(path,comparator,1);
        assertEquals(heap.getSize(),heap1.getSize());
    }
    @Test
    public void IncorrectVersionSerializeJson(@TempDir Path temp) throws IOException {
        Serializer json = new JsonSerializer();
        Path path = temp.resolve("otherversion.json");
        try(InputStream input = getClass().getResourceAsStream("/testsFiles/otherversion.json")){
            assertNotNull(input,"Файл не найден");
            Files.copy(input,path);
            SerialException exception =  assertThrows(SerialException.class,()->json.load(path,comparator,21));
            assertEquals(SerialException.SerialErrCode.UNSUPPORTED_VERSION,exception.getCode());
        }

    }
    @Test
    public void IncorrectVersionSerializeBin(@TempDir Path temp) throws IOException {
        Serializer bin = new BinSerializable();
        Path path = temp.resolve("otherversion.bin");
        try(InputStream input = getClass().getResourceAsStream("/testsFiles/otherversion.bin")){
            assertNotNull(input,"Файл не найден");
            Files.copy(input,path);
            SerialException exception =  assertThrows(SerialException.class,()->bin.load(path,comparator,21));
            assertEquals(SerialException.SerialErrCode.UNSUPPORTED_VERSION,exception.getCode());
        }

    }
    @Test
    public void wrongFileBin(@TempDir Path temp) throws IOException{
        Serializer bin = new BinSerializable();
        Path path = temp.resolve("incorrect.bin");
        try(InputStream input = getClass().getResourceAsStream("/testsFiles/incorrect.bin")){
            assertNotNull(input,"Файл не найден");
            Files.copy(input,path);
            SerialException exception =  assertThrows(SerialException.class,()->bin.load(path,comparator,20));
            assertEquals(SerialException.SerialErrCode.FILE_IO,exception.getCode());
        }
    }
    @Test
    public void wrongFileJson(@TempDir Path temp) throws IOException{
        Serializer json = new JsonSerializer();
        Path path = temp.resolve("incorrect.json");
        try(InputStream input = getClass().getResourceAsStream("/testsFiles/incorrect.json")){
            assertNotNull(input,"Файл не найден");
            Files.copy(input,path);
            SerialException exception =  assertThrows(SerialException.class,()->json.load(path,comparator,20));
            assertEquals(SerialException.SerialErrCode.FILE_IO,exception.getCode());
        }
    }
    @Test
    public void IncorrectFieldJson(@TempDir Path temp) throws IOException{
        Serializer json = new JsonSerializer();
        Path path = temp.resolve("incorrectFields.json");
        try(InputStream input = getClass().getResourceAsStream("/testsFiles/incorrectFields.json")){
            assertNotNull(input,"Файл не найден");
            Files.copy(input,path);
            SerialException exception =  assertThrows(SerialException.class,()->json.load(path,comparator,20));
            assertEquals(SerialException.SerialErrCode.EMPTY_FIELD,exception.getCode());
        }
    }
    @Test
    public void IncorrectFieldBin(@TempDir Path temp) throws IOException{
        Serializer bin = new BinSerializable();
        Path path = temp.resolve("incorrectFields.bin");
        try(InputStream input = getClass().getResourceAsStream("/testsFiles/incorrectFields.bin")){
            assertNotNull(input,"Файл не найден");
            Files.copy(input,path);
            SerialException exception =  assertThrows(SerialException.class,()->bin.load(path,comparator,20));
            assertEquals(SerialException.SerialErrCode.EMPTY_FIELD,exception.getCode());
        }
    }





}
