package lab2.io.serializable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lab2.exception.SerialException;
import lab2.heap.BinHeap;
import lab2.tickets.Ticket;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;


public class JsonSerializer implements Serializer{
    private final ObjectMapper mapper;
    public JsonSerializer() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    @Override
    public void save(Path path, BinHeap<Ticket> heap,long version) throws SerialException {
        if(heap==null){
            throw new SerialException("heap is null", SerialException.SerialErrCode.UNKNOW_CLASS);
        }
        Ticket[] tickets = getArrTickets(heap);
        SerialFileClass serial = new SerialFileClass(version,tickets);
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(),serial);
        } catch (IOException e) {
            throw new SerialException(e.getMessage(), SerialException.SerialErrCode.FILE_IO,e);
        }

    }
    @Override
    public BinHeap<Ticket> load(Path path, Comparator<Ticket> comparator,long version) throws SerialException {
        SerialFileClass serial;
        try{
            serial= mapper.readValue(path.toFile(),SerialFileClass.class);
            if(serial==null){
                throw new NullPointerException("Null object");
            }
        }catch (NullPointerException e){
            throw new SerialException(e.getMessage(), SerialException.SerialErrCode.EMPTY_FIELD,e);
        }catch (IOException e) {
            throw new SerialException(e.getMessage(), SerialException.SerialErrCode.FILE_IO,e);
        }
        if(serial.getVersion()!=version){
            throw new SerialException("Incorrect version file", SerialException.SerialErrCode.UNSUPPORTED_VERSION);
        }
        Ticket[] tickets = serial.getTickets();
        if(tickets==null){
            throw new SerialException("Don't find tickets", SerialException.SerialErrCode.EMPTY_TICKET);
        }
        BinHeap<Ticket> heap = new BinHeap<>(comparator);
            for (int i = 0; i < tickets.length; i++) {
                TicketValidator.validate(tickets[i], i);
                heap.add(tickets[i]);
            }
        return heap;

    }
    @Override
    public String format(){
        return "json";
    }
    private Ticket[] getArrTickets( BinHeap<Ticket> heap){
        int size = heap.getSize();
        Ticket[] tickets = new Ticket[size];
        for(int i = 0;i<size;i++){
            tickets[i]=  heap.get(i);
        }
        return tickets;
    }
}
