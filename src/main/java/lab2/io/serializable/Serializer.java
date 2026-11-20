package lab2.io.serializable;

import lab2.exception.SerialException;
import lab2.heap.BinHeap;
import lab2.tickets.Ticket;

import java.nio.file.Path;
import java.util.Comparator;

public interface Serializer {
    BinHeap<Ticket> load(Path path, Comparator<Ticket> comparator) throws SerialException;
    void save(Path path, BinHeap<Ticket> heap,int version) throws SerialException;
    String format();
    int getLoadVersion();
}
