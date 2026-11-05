package lab2.gui;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lab2.tickets.*;

import java.time.LocalTime;


public class TicketTableConfig {
    public static void config(TableView<Ticket> table){
        TableColumn<Ticket, Integer> colCard = new TableColumn<>("Card Number");
        TableColumn<Ticket, String> type = new TableColumn<>("Type");
        TableColumn<Ticket, String> adres = new TableColumn<>("Address");
        TableColumn<Ticket, String> colName = new TableColumn<>("Name");
        TableColumn<Ticket, Integer> office = new TableColumn<>("Office");
        TableColumn<Ticket, Integer> priority = new TableColumn<>("Priority");
        TableColumn<Ticket, LocalTime> time = new TableColumn<>("time");
        colCard.setCellValueFactory((cellData) -> {
            Ticket val = cellData.getValue();
            return new ReadOnlyObjectWrapper<>(val.getCardNumber());
        });
        colName.setCellValueFactory((cellData) -> {
            Ticket val = cellData.getValue();
            return new ReadOnlyStringWrapper(val.getName());
        });
        office.setCellValueFactory((cellData) -> {
            Ticket val = cellData.getValue();
            return new ReadOnlyObjectWrapper<>(val.getOffice());
        });
        priority.setCellValueFactory((cellData) -> {
            Ticket val = cellData.getValue();
            return new ReadOnlyObjectWrapper<>(val.getPriority());
        });
        time.setCellValueFactory((cellData) -> {
            Ticket val = cellData.getValue();
            return new ReadOnlyObjectWrapper<>(val.getTime());
        });
        type.setCellValueFactory(cellData -> {
            Ticket val = cellData.getValue();
            if (val instanceof RegularTicket) {
                return new ReadOnlyStringWrapper(TicketType.REGULAR.name());
            } else if (val instanceof CloseTicket) {
                return new ReadOnlyStringWrapper(TicketType.CLOSED.name());
            } else if (val instanceof HomeTicket) {
                return new ReadOnlyStringWrapper(TicketType.HOME.name());
            }
            return new ReadOnlyStringWrapper("NONE");
        });
        adres.setCellValueFactory(cellData -> {
            Ticket tk = cellData.getValue();
            if (tk instanceof HomeTicket) {
                return new ReadOnlyStringWrapper(((HomeTicket) tk).getAdres());
            }
            return new ReadOnlyStringWrapper("");
        });
        colName.setMinWidth(180);
        adres.setMinWidth(180);
        table.getColumns().addAll(type, colCard, colName, office, priority, time, adres);
    }
}
