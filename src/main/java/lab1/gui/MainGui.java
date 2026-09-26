package lab1.gui;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lab1.exception.CsvException;
import lab1.io.CsvLoadResult;
import lab1.io.CsvLoader;
import lab1.io.CsvSaver;
import lab1.tickets.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;
public class MainGui extends Application {
    private static class TicketForm {
        final Dialog<ButtonType> dialog = new Dialog<>();
        final ComboBox<TicketType> comboType = new ComboBox<>();
        final TextField cardNum = new TextField();
        final TextField name = new TextField();
        final TextField office = new TextField();
        final ComboBox<Integer> priority = new ComboBox<>();
        final TextField time = new TextField();
        final TextField address = new TextField();
    }
    @Override
    public void start(Stage stage){
        BorderPane borderPane = new BorderPane();
        TableView<Ticket> table = new TableView<>();
        TableColumn<Ticket, Integer> colCard = new TableColumn<>("Card Number");
        TableColumn<Ticket, String> type = new TableColumn<>("Type");
        TableColumn<Ticket, String> adres = new TableColumn<>("Address");
        TableColumn<Ticket, String> colName = new TableColumn<>("Name");
        TableColumn<Ticket, Integer> office = new TableColumn<>("Office");
        TableColumn<Ticket, Integer> priority = new TableColumn<>("Priority");
        TableColumn<Ticket, LocalTime> time = new TableColumn<>("time");
        colCard.setCellValueFactory(new PropertyValueFactory<>("cardNumber"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        office.setCellValueFactory(new PropertyValueFactory<>("office"));
        priority.setCellValueFactory(new PropertyValueFactory<>("priority"));
        time.setCellValueFactory(new PropertyValueFactory<>("time"));
        type.setCellValueFactory(cellData -> {
            Ticket val = cellData.getValue();
            if(val instanceof RegularTicket){
                return new ReadOnlyStringWrapper(TicketType.REGULAR.name());
            }else if(val instanceof CloseTicket){
                return new ReadOnlyStringWrapper(TicketType.CLOSED.name());
            }else if(val instanceof HomeTicket){
                return new ReadOnlyStringWrapper(TicketType.HOME.name());
            }
            return new ReadOnlyStringWrapper("NONE");
        });
        adres.setCellValueFactory(cellData -> {
            Ticket tk = cellData.getValue();
            if(tk instanceof HomeTicket){
                return new ReadOnlyStringWrapper(((HomeTicket) tk).getAdres());
            }
            return new ReadOnlyStringWrapper("");
        });
        colName.setMinWidth(180);
        adres.setMinWidth(180);
        table.getColumns().addAll(type,colCard,colName,office,priority,time,adres);
        ObservableList<Ticket> array = FXCollections.observableArrayList();
        table.setItems(array);
        borderPane.setCenter(table);

        HBox hBox = new HBox(40);
        Button loadcsv=new Button("Load CSV");
        Button savecsv=new Button("Save CSV");
        Button add=new Button("add");
        Button editable=new Button("Edit");
        editable.setDisable(true);
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldTicket, newTicket) -> {
                    if (newTicket instanceof Editable) {
                        editable.setDisable(false);
                    } else {
                        editable.setDisable(true);
                    }
                });
        hBox.getChildren().addAll(loadcsv,savecsv,add,editable);
        borderPane.setBottom(hBox);
        hBox.setAlignment(Pos.CENTER);

        loadcsv.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose CSV");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Csv Files","*.csv"));
            File file = fileChooser.showOpenDialog(stage);
            if(file!=null){
                try {
                    CsvLoadResult res  = CsvLoader.load(file.toPath());
                    array.setAll(res.getTickets());
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Load information");
                    alert.setHeaderText("Successful loaded: "+res.getTickets().size());
                    StringBuilder err = new StringBuilder();
                    for(CsvException e: res.getErrors()){
                        err.append("Lines "+e.getLineNumber()+" -> "+e.getCode()+" : "+e.getMessage()+"\n");
                    }
                    alert.setContentText("Error Logs:\n"+err);
                    alert.setWidth(500);
                    alert.setHeight(300);
                    alert.showAndWait();
                } catch (IOException e) {
                    Alert al = errAlert("Error IO",null,e.getMessage());
                    al.showAndWait();
                }
            }
        });
        savecsv.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save CSV");
            File file = fileChooser.showSaveDialog(stage);
            if(file!=null){
                try {
                    CsvSaver.save(array,file.toPath());
                } catch (IOException e) {
                    Alert al = errAlert("Error IO",null,e.getMessage());
                    al.showAndWait();
                }catch (IllegalArgumentException e){
                    Alert al = errAlert("Save Error",null,e.getMessage());
                    al.showAndWait();
                }

            }
        });

        add.setOnAction(event -> {
            TicketForm form = editdialog("add Field");
            Optional<ButtonType> showres = form.dialog.showAndWait();
            if(showres.isPresent()&&showres.get()==ButtonType.OK){
                try {
                    Ticket res = applyTicketForm(form,null);
                    array.add(res);
                }catch (IllegalArgumentException | DateTimeParseException e){
                    Alert alert = errAlert("Incorrect argument",null,e.getMessage());
                    alert.showAndWait();
                }
            }
        });
        editable.setOnAction(event -> {
            Ticket selected = table.getSelectionModel().getSelectedItem();
            try {
                if (selected==null){
                    throw new IllegalArgumentException("Field is null");
                }
                TicketForm form = editdialog("edit Field");
                form.comboType.setDisable(true);
                form.cardNum.setText(String.valueOf(selected.getCardNumber()));
                form.name.setText(selected.getName());
                form.office.setText(String.valueOf(selected.getOffice()));
                form.priority.getSelectionModel().select(selected.getPriority());
                form.time.setText(String.valueOf(selected.getTime()));
                if(selected instanceof HomeTicket home){
                    form.comboType.getSelectionModel().select(TicketType.HOME);
                    form.address.setText(home.getAdres());
                    form.address.setDisable(false);
                }else {
                    form.comboType.getSelectionModel().select(TicketType.REGULAR);
                }
                Optional<ButtonType> showres = form.dialog.showAndWait();
                if (showres.isPresent() && showres.get() == ButtonType.OK) {
                    applyTicketForm(form,selected);
                    table.refresh();
                }
            }catch (IllegalArgumentException | DateTimeParseException e){
                Alert alert = errAlert("Incorrect argument",null,e.getMessage());
                alert.showAndWait();
            }
        });




        Scene scene = new Scene(borderPane,900,500);
        stage.setTitle("Patients Tickets");
        stage.setScene(scene);
        stage.show();
    }
    private Alert errAlert(String title,String header,String context){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(context);
        return alert;
    }
    private TicketForm editdialog(String title){
        TicketForm form = new TicketForm();
        GridPane grid = new GridPane();
        form.dialog.setTitle(title);
        form.dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        grid.setHgap(15);
        grid.setVgap(20);
        form.comboType.getItems().addAll(TicketType.REGULAR, TicketType.HOME);
        form.comboType.getSelectionModel().selectFirst();
        form.priority.getItems().addAll(0, 1, 2, 3);
        form.priority.getSelectionModel().selectFirst();
        grid.add(form.comboType, 1, 0);
        grid.add(new Label("Type: "), 0, 0);
        grid.add(form.cardNum, 1, 1);
        grid.add(new Label("Card Number: "), 0, 1);
        grid.add(form.name, 1, 2);
        grid.add(new Label("Name: "), 0, 2);
        grid.add(form.office, 1, 3);
        grid.add(new Label("Office: "), 0, 3);
        grid.add(form.priority, 1, 4);
        grid.add(new Label("Priority: "), 0, 4);
        grid.add(new Label("Time: "), 0, 5);
        grid.add(form.time, 1, 5);
        grid.add(form.address, 1, 6);
        grid.add(new Label("Address: "), 0, 6);
        form.dialog.getDialogPane().setContent(grid);
        form.dialog.setWidth(800);
        form.dialog.setHeight(600);
        form.address.setDisable(true);
        form.comboType.setOnAction(event2 -> {
            TicketType typed = form.comboType.getValue();
            if (typed == TicketType.REGULAR) {
                form.address.setDisable(true);
            } else {
                form.address.setDisable(false);
            }
        });
        return form;
    }
    private Ticket applyTicketForm(TicketForm form, Ticket selected){
        TicketType typed = form.comboType.getValue();
        int cardNumber = Integer.parseInt(form.cardNum.getText());
        String addname = form.name.getText();
        if (addname.isBlank()) {
            throw new IllegalArgumentException("Incorrect field name");
        }
        int off = Integer.parseInt(form.office.getText());
        int priOrity = form.priority.getValue();
        LocalTime tme = LocalTime.parse(form.time.getText());
        if(selected==null) {
            if (typed == TicketType.REGULAR) {
                return new RegularTicket(cardNumber, off, addname, priOrity, tme);

            } else {
                String adress = form.address.getText();
                if (adress.isBlank()) {
                    throw new IllegalArgumentException("Incorrect field address");
                }
                return new HomeTicket(cardNumber, off, addname, priOrity, tme, adress);
            }
        }else{
            if(selected instanceof HomeTicket home){
                String adress = form.address.getText();
                if (adress.isBlank()) {
                    throw new IllegalArgumentException("Incorrect field address");
                }
                home.setAdres(adress);
            }
            selected.setCardNumber(cardNumber);
            selected.setName(addname);
            selected.setOffice(off);
            selected.setPriority(priOrity);
            selected.setTime(tme);



            }
            return selected;
        }

}



