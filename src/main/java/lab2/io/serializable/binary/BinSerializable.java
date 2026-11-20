package lab2.io.serializable.binary;
import lab2.exception.SerialException;
import lab2.heap.BinHeap;
import lab2.io.serializable.*;
import lab2.tickets.Ticket;
import java.io.*;
import java.nio.file.Path;
import java.util.Comparator;

import static lab2.io.serializable.SchemaVersions.CURRENT_VERSION;
import static lab2.io.serializable.SchemaVersions.MIN_SUPPORTED_VERSION;
public class BinSerializable implements Serializer {
    private int loadVersion;

    public int getLoadVersion() {
        return loadVersion;
    }

    @Override
    public BinHeap<Ticket> load(Path path, Comparator<Ticket> comparator) throws SerialException {
        SerialFileClass serialFileClass;
        try(VersionedObjectInputStream objectInputStream = new VersionedObjectInputStream(new FileInputStream(path.toFile()))){
            int version=objectInputStream.getTargetVersion();
            if(version>CURRENT_VERSION||version<MIN_SUPPORTED_VERSION){
                throw new SerialException("Illegal field", SerialException.SerialErrCode.UNSUPPORTED_VERSION);
            }
            loadVersion=version;
            serialFileClass= (SerialFileClass) objectInputStream.readObject();
            serialFileClass.setSchema_version(version);
            if(serialFileClass==null){
                throw new NullPointerException("Null object");
            }
        }catch (ClassNotFoundException e){
            throw new SerialException(e.getMessage(), SerialException.SerialErrCode.UNKNOW_CLASS,e);
        }catch (ClassCastException e){
            throw new SerialException(e.getMessage(), SerialException.SerialErrCode.CAST_CLASS,e);
        } catch (NullPointerException e){
            throw new SerialException(e.getMessage(), SerialException.SerialErrCode.EMPTY_FIELD,e);
        }catch (SerialException e) {
            throw e;
        } catch (IOException e) {
            throw new SerialException(e.getMessage(), SerialException.SerialErrCode.FILE_IO,e);
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


    public void save(Path path, BinHeap<Ticket> heap, int version) throws SerialException {
        if(heap==null){
            throw new SerialException("heap is null", SerialException.SerialErrCode.UNKNOW_CLASS);
        }
        if(version>CURRENT_VERSION||version<MIN_SUPPORTED_VERSION){
            throw new SerialException("Illegal field", SerialException.SerialErrCode.UNSUPPORTED_VERSION);
        }

        SerialFileClass serial = new SerialFileClass(version,getArrTickets(heap));
        try (VersionedObjectOutputStream out = new VersionedObjectOutputStream(new FileOutputStream(path.toFile()), version)) {
            out.writeObject(serial);
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
