package lab2.io.serializable.json;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lab2.exception.SerialException;
import lab2.heap.BinHeap;
import lab2.io.serializable.SerialFileClass;
import lab2.io.serializable.Serializer;
import lab2.io.serializable.TicketValidator;
import lab2.tickets.Ticket;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import com.fasterxml.jackson.databind.JsonNode;
import static lab2.io.serializable.SchemaVersions.CURRENT_VERSION;
import static lab2.io.serializable.SchemaVersions.MIN_SUPPORTED_VERSION;

public class JsonSerializer implements Serializer {
    private final ObjectMapper mapper;
    private int loadVersion;

    public int getLoadVersion() {
        return loadVersion;
    }

    public JsonSerializer() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
        mapper.disable(DeserializationFeature.ACCEPT_FLOAT_AS_INT);
    }
    @Override
    public void save(Path path, BinHeap<Ticket> heap, int version) throws SerialException {
        if(heap==null){
            throw new SerialException("heap is null", SerialException.SerialErrCode.UNKNOW_CLASS);
        }
        if (version < MIN_SUPPORTED_VERSION || version > CURRENT_VERSION) {
            throw new SerialException("Unsupported target version: " + version, SerialException.SerialErrCode.UNSUPPORTED_VERSION);
        }
        Ticket[] tickets = getArrTickets(heap);
        SerialFileClass serial = new SerialFileClass(CURRENT_VERSION,tickets);
        JsonNode node = mapper.valueToTree(serial);
        MigrationManager.checkDownMigration(node,version,checkAndGetVersion(node));
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(),node);
        } catch (IOException e) {
            throw new SerialException(e.getMessage(), SerialException.SerialErrCode.FILE_IO,e);
        }

    }
    @Override
    public BinHeap<Ticket> load(Path path, Comparator<Ticket> comparator) throws SerialException {
        SerialFileClass serial;
        try{
            JsonNode node =   mapper.readTree(path.toFile());
            loadVersion = checkAndGetVersion(node);
            MigrationManager.checkUpMigration(node,loadVersion);
            validateJsonTypes(node);
            serial = mapper.treeToValue(node,SerialFileClass.class);
            if(serial==null){
                throw new NullPointerException("Null object");
            }
        }catch (NullPointerException e){
            throw new SerialException(e.getMessage(), SerialException.SerialErrCode.EMPTY_FIELD,e);
        }catch (SerialException e) {
            throw e;
        }
        catch (IOException e) {
            throw new SerialException(e.getMessage(), SerialException.SerialErrCode.FILE_IO,e);
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
    private static int checkAndGetVersion(JsonNode root) throws SerialException {
        JsonNode schemaVersion = root.get("schema_version");
        if(schemaVersion==null||!schemaVersion.isIntegralNumber()||!schemaVersion.canConvertToInt()){
            throw new SerialException("Illegal field", SerialException.SerialErrCode.UNSUPPORTED_VERSION);
        }
        int version = schemaVersion.asInt();
        if(version>CURRENT_VERSION||version<MIN_SUPPORTED_VERSION){
            throw new SerialException("Illegal field", SerialException.SerialErrCode.UNSUPPORTED_VERSION);
        }
        return version;
    }

    private static void validateJsonTypes(JsonNode root) throws SerialException {
        JsonNode tickets = root.path("tickets");
        if (!tickets.isArray()) {
            throw new SerialException("Tickets must be an array", SerialException.SerialErrCode.INCORRECT_FIELD);
        }
        for (JsonNode ticket : tickets) {
            if (!ticket.isObject() || !ticket.path("priority").isIntegralNumber() || !ticket.path("priority").canConvertToInt() || !ticket.path("name").isTextual() || !ticket.path("comment").isTextual() || !ticket.path("confirmed").isBoolean()) {
                throw new SerialException("Invalid ticket field type", SerialException.SerialErrCode.INCORRECT_FIELD);
            }
            if (!ticket.path("cardNumber").isIntegralNumber() || !ticket.path("cardNumber").canConvertToInt() || !ticket.path("office").isIntegralNumber() || !ticket.path("office").canConvertToInt()) {
                throw new SerialException("Invalid numeric field", SerialException.SerialErrCode.INCORRECT_FIELD);
            }
            if (!ticket.path("time").isTextual()) {
                throw new SerialException("Invalid time field", SerialException.SerialErrCode.INCORRECT_FIELD);
            }
            if (ticket.has("adres") && !ticket.path("adres").isTextual()) {
                throw new SerialException("Invalid address field", SerialException.SerialErrCode.INCORRECT_FIELD);
            }
        }
    }

}

