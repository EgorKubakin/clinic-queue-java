package lab2;

import javafx.application.Application;
import lab2.gui.*;
import lab2.heap.BinHeap;
import lab2.io.serializable.BinSerializable;
import lab2.io.serializable.Serializer;
import lab2.tickets.CloseTicket;
import lab2.tickets.HomeTicket;
import lab2.tickets.RegularTicket;
import lab2.tickets.Ticket;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.Comparator;

public class Main {
    public static void main(String[] args) throws IOException {
        Application.launch(MainGui.class, args);
    }
}
