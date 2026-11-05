package lab2.io.serializable;

import lab2.exception.SerialException;
import lab2.heap.BinHeap;
import lab2.tickets.Ticket;

import java.nio.file.Path;
import java.util.Comparator;

public interface Serializer {
    BinHeap<Ticket> load(Path path, Comparator<Ticket> comparator,long version) throws SerialException;
    void save(Path path, BinHeap<Ticket> heap,long version) throws SerialException;
    String format();
}
