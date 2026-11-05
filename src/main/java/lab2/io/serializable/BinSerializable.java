package lab2.io.serializable;
import lab2.exception.SerialException;
import lab2.heap.BinHeap;
import lab2.tickets.Ticket;
import java.io.*;
import java.nio.file.Path;
import java.util.Comparator;

public class BinSerializable implements Serializer {
    @Override
    public BinHeap<Ticket> load(Path path, Comparator<Ticket> comparator, long version) throws SerialException {
        SerialFileClass serialFileClass;
        try(ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(path.toFile()))){
            serialFileClass= (SerialFileClass) objectInputStream.readObject();
            if(serialFileClass==null){
                throw new NullPointerException("Null object");
            }
        }catch (ClassNotFoundException e){
            throw new SerialException(e.getMessage(), SerialException.SerialErrCode.UNKNOW_CLASS,e);
        }catch (ClassCastException e){
            throw new SerialException(e.getMessage(), SerialException.SerialErrCode.CAST_CLASS,e);
        } catch (NullPointerException e){
            throw new SerialException(e.getMessage(), SerialException.SerialErrCode.EMPTY_FIELD,e);
        } catch (IOException e) {
            throw new SerialException(e.getMessage(), SerialException.SerialErrCode.FILE_IO,e);
        }
        if(serialFileClass.getVersion()!=version){
            throw new SerialException("Incorrect version file", SerialException.SerialErrCode.UNSUPPORTED_VERSION);
        }
        Ticket[] tickets = serialFileClass.getTickets();
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
    public void save(Path path, BinHeap<Ticket> heap, long version) throws SerialException {
        if(heap==null){
            throw new SerialException("heap is null", SerialException.SerialErrCode.UNKNOW_CLASS);
        }
        SerialFileClass serial = new SerialFileClass(version,getArrTickets(heap));
        try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(path.toFile()))){
            objectOutputStream.writeObject(serial);
        }catch (IOException e) {
            throw new SerialException(e.getMessage(), SerialException.SerialErrCode.FILE_IO,e);
        }
    }
    private Ticket[] getArrTickets( BinHeap<Ticket> heap){
        int size = heap.getSize();
        Ticket[] tickets = new Ticket[size];
        for(int i = 0;i<size;i++){
            tickets[i]=  heap.get(i);
        }
        return tickets;
    }

    @Override
    public String format() {
        return "Bin";
    }
}
