package lab2.io.serializable.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lab2.exception.SerialException;
import lab2.io.serializable.SchemaVersions;

import static lab2.io.serializable.SchemaVersions.CURRENT_VERSION;
import static lab2.io.serializable.SchemaVersions.MIN_SUPPORTED_VERSION;

public class MigrationManager {

    public static void checkUpMigration(JsonNode root,int version) throws SerialException {
        while (version<CURRENT_VERSION){
            if(version==1L){
                migrateV1toV2(root);
            }else if(version==2L){
                migrateV2toV3(root);
            }else {
                throw new SerialException("Illegal field", SerialException.SerialErrCode.UNSUPPORTED_VERSION);
            }
            version++;

        }
    }
    public static void checkDownMigration(JsonNode root,int targetVersion,int version) throws SerialException {
        while (version>targetVersion){
            if(version==3){
                migrateV3toV2(root);
            }else if(version==2){
                migrateV2toV1(root);
            }else {
                throw new SerialException("Illegal field", SerialException.SerialErrCode.UNSUPPORTED_VERSION);
            }
            version--;
        }

    }
    private static void migrateV3toV2(JsonNode root) throws SerialException {
        JsonNode array = getArray(root);
        for (JsonNode ticket: array){
            if(ticket.has("confirmed")){
                ((ObjectNode) ticket).remove("confirmed");
            }
        }
        ((ObjectNode) root).put("schema_version",2);
    }
    private static void migrateV2toV1(JsonNode root) throws SerialException {
        JsonNode array = getArray(root);
        for (JsonNode ticket: array){
            if(ticket.has("comment")){
                ((ObjectNode) ticket).remove("comment");
            }
        }
        ((ObjectNode) root).put("schema_version",1);
    }

    private static void migrateV1toV2(JsonNode root) throws SerialException {
        JsonNode array = getArray(root);
        for(JsonNode ticket: array){
            if(!ticket.hasNonNull("comment")){
                ((ObjectNode) ticket).put("comment","None");
            }
        }
        ((ObjectNode) root).put("schema_version",2);
    }
    private static void migrateV2toV3(JsonNode root) throws SerialException {
        JsonNode array = getArray(root);
        for(JsonNode ticket: array){
            if(!ticket.hasNonNull("confirmed")){
                ((ObjectNode) ticket).put("confirmed",false);
            }
        }
        ((ObjectNode) root).put("schema_version",3);
    }
    private static JsonNode getArray(JsonNode root) throws SerialException {
        JsonNode array;
        if(root.hasNonNull("tickets")&&root.get("tickets").isArray()) {
            array = root.get("tickets");
            return array;
        }else {
            throw new SerialException("Incorrect field Tickets", SerialException.SerialErrCode.INCORRECT_FIELD);
        }
    }
}
