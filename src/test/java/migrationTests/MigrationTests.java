
package migrationTests;

import lab2.exception.SerialException;
import lab2.heap.BinHeap;
import lab2.io.serializable.Serializer;
import lab2.io.serializable.binary.BinSerializable;
import lab2.io.serializable.json.JsonSerializer;
import lab2.tickets.CloseTicket;
import lab2.tickets.HomeTicket;
import lab2.tickets.RegularTicket;
import lab2.tickets.Ticket;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.Comparator;
import static org.junit.jupiter.api.Assertions.*;

public class MigrationTests {

    private final Comparator<Ticket> comparator = (ticket1, ticket2) -> {
        int res = Integer.compare(
                ticket1.getPriority(),
                ticket2.getPriority()
        );

        if (res == 0) {
            res = ticket1.getTime().compareTo(ticket2.getTime());
        }
        return res;
    };

    @Test
    public void binFromV1ToV3(@TempDir Path temp) throws IOException {
        Serializer bin = new BinSerializable();
        Path path = copyFile(temp, "bin_v1.bin");
        BinHeap<Ticket> heap = bin.load(path, comparator);
        assertEquals(1, bin.getLoadVersion());
        assertTickets(heap, 1);
    }

    @Test
    public void jsonFromV1ToV3(@TempDir Path temp) throws IOException {
        Serializer json = new JsonSerializer();
        Path path = copyFile(temp, "json_v1.json");
        BinHeap<Ticket> heap = json.load(path, comparator);
        assertEquals(1, json.getLoadVersion());
        assertTickets(heap, 1);
    }

    @Test
    public void binFromV2ToV3(@TempDir Path temp) throws IOException {
        Serializer bin = new BinSerializable();
        Path path = copyFile(temp, "bin_v2.bin");
        BinHeap<Ticket> heap = bin.load(path, comparator);
        assertEquals(2, bin.getLoadVersion());
        assertTickets(heap, 2);
    }

    @Test
    public void jsonFromV2ToV3(@TempDir Path temp) throws IOException {
        Serializer json = new JsonSerializer();
        Path path = copyFile(temp, "json_v2.json");
        BinHeap<Ticket> heap = json.load(path, comparator);
        assertEquals(2, json.getLoadVersion());
        assertTickets(heap, 2);
    }

    @Test
    public void binCurrentVersionKeepsData(@TempDir Path temp) throws IOException {
        Serializer bin = new BinSerializable();
        Path path = copyFile(temp, "bin_v3.bin");
        BinHeap<Ticket> heap = bin.load(path, comparator);
        assertEquals(3, bin.getLoadVersion());
        assertTickets(heap, 3);
    }

    @Test
    public void jsonCurrentVersionKeepsData(@TempDir Path temp) throws IOException {
        Serializer json = new JsonSerializer();
        Path path = copyFile(temp, "json_v3.json");
        BinHeap<Ticket> heap = json.load(path, comparator);
        assertEquals(3, json.getLoadVersion());
        assertTickets(heap, 3);
    }

    @Test
    public void jsonV1IncorrectTicketsField(@TempDir Path temp) throws IOException {
        Serializer json = new JsonSerializer();
        Path path = copyFile(temp, "json_v1_wrong_tickets.json");
        SerialException error = assertThrows(SerialException.class, () -> json.load(path, comparator));
        assertEquals(SerialException.SerialErrCode.INCORRECT_FIELD, error.getCode());
    }

    @Test
    public void binV2MissingRequiredName(@TempDir Path temp) throws IOException {
        Serializer bin = new BinSerializable();
        Path path = copyFile(temp, "bin_v2_missing_name.bin");
        SerialException error = assertThrows(SerialException.class, () -> bin.load(path, comparator));
        assertEquals(SerialException.SerialErrCode.EMPTY_FIELD, error.getCode());
    }

    private Path copyFile(Path temp, String filename) throws IOException {
        Path path = temp.resolve(filename);
        try (InputStream input = getClass().getResourceAsStream("/testsFiles/migration/" + filename)) {
            assertNotNull(input, "Test file not found: " + filename);
            Files.copy(input, path);
        }
        return path;
    }

    private void assertTickets(BinHeap<Ticket> heap, int sourceVersion) {
        assertEquals(3, heap.getSize());
        Ticket regular = heap.pop();
        Ticket home = heap.pop();
        Ticket close = heap.pop();
        assertTicket(regular, RegularTicket.class, 120, 12, "Egor", 0, LocalTime.of(7, 30), sourceVersion == 1 ? "None" : "Reception", sourceVersion == 3);
        assertTicket(home, HomeTicket.class, 130, 1, "Ivan", 1, LocalTime.of(8, 30), sourceVersion == 1 ? "None" : "Home visit", sourceVersion == 3);
        HomeTicket homeTicket = assertInstanceOf(HomeTicket.class, home);
        assertEquals("Green Street 33", homeTicket.getAdres());
        assertTicket(close, CloseTicket.class, 200, 15, "Bob", 2, LocalTime.of(12, 30), sourceVersion == 1 ? "None" : "Closed record", sourceVersion == 3);
        assertEquals(0, heap.getSize());
    }

    private void assertTicket(Ticket actual, Class<? extends Ticket> expectedClass, int cardNumber, int office, String name, int priority, LocalTime time, String comment, boolean confirmed) {
        assertAll("Check " + name, () -> assertInstanceOf(expectedClass, actual), () -> assertEquals(cardNumber, actual.getCardNumber()), () -> assertEquals(office, actual.getOffice()), () -> assertEquals(name, actual.getName()), () -> assertEquals(priority, actual.getPriority()), () -> assertEquals(time, actual.getTime()), () -> assertEquals(comment, actual.getComment()), () -> assertEquals(confirmed, actual.isConfirmed()));
    }
}
