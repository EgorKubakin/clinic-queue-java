package lab2.gui;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lab2.generator.TicketGenerator;
import lab2.generator.TicketGeneratorTask;
import lab2.heap.BinHeap;
import lab2.io.CsvLoadResult;
import lab2.io.CsvLoader;
import lab2.io.CsvSaver;
import lab2.tickets.*;
import javafx.scene.control.ScrollPane;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;
import java.util.Optional;

public class MainGui extends Application {
    private BinHeap<Ticket> binHeap;
    private static final int MAX_TABLE_ITEMS = 1000;
    private int countUrgent = 0;
    private boolean heapWriteLocked = false;
    private int activeSaves = 0;

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
    public void start(Stage stage) {
        binHeap = createHeap();
        BorderPane borderPane = new BorderPane();
        TableView<Ticket> table = new TableView<>();
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
        ObservableList<Ticket> array = FXCollections.observableArrayList();
        table.setItems(array);
        HeapVisualizer canvas = new HeapVisualizer();
        canvas.setWidth(600);
        canvas.setHeight(900);
        ScrollPane scrollPane = new ScrollPane(canvas);
        SplitPane splitPane = new SplitPane(table, scrollPane);
        splitPane.setDividerPositions(0.45);
        borderPane.setCenter(splitPane);
        table.setMinWidth(0);
        scrollPane.setMinWidth(0);
        HBox hBox = new HBox(40);
        HBox hBox1 = new HBox(40);
        HBox hBox2 = new HBox(30);
        HBox hBox3 = new HBox(40);
        VBox vBox = new VBox(20);
        Button loadcsv = new Button("Load CSV");
        Button savecsv = new Button("Save CSV");
        Button add = new Button("add");
        Button editable = new Button("Edit");
        Button delete = new Button("Delete");
        Button nextTicket = new Button("Next Ticket");
        Button search = new Button("Search");
        Label label = new Label("Card Number:");
        TextField searchText = new TextField();
        Label percentProgress = new Label("");
        searchText.setPromptText("Card number:");
        Label generateLb = new Label("Generate tickets:");
        TextField generateText = new TextField();
        generateText.setPromptText("Input count tickets");
        Button generate = new Button("Generate");
        Button cancel = new Button("Cancel");
        Button getU = new Button("Get");
        Label mostLb = new Label("Most Urgent:");
        Button changeP = new Button("Change Priority");
        Button merge = new Button("Merge");
        TextField mostK = new TextField();
        mostK.setPromptText("Input count: ");
        ProgressBar progressBar = new ProgressBar();
        editable.setDisable(true);
        delete.setDisable(true);
        cancel.setDisable(true);
        changeP.setDisable(true);
        listenerTableProperty(table, delete, editable, changeP);
        hBox.getChildren().addAll(loadcsv, savecsv, add, editable, nextTicket, delete);
        hBox1.getChildren().addAll(label, searchText, search);
        hBox2.getChildren().addAll(generateLb, generateText, generate, cancel, progressBar, percentProgress);
        hBox3.getChildren().addAll(mostLb, mostK, getU, changeP, merge);
        vBox.getChildren().addAll(hBox, hBox1, hBox2, hBox3);
        borderPane.setBottom(vBox);
        hBox.setAlignment(Pos.CENTER);
        hBox1.setAlignment(Pos.CENTER);
        hBox2.setAlignment(Pos.CENTER);
        hBox3.setAlignment(Pos.CENTER);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10));
        merge.setOnAction(e -> {
            handleMerge(loadcsv, generate, merge, add, editable, delete, nextTicket, changeP, cancel, table, stage, progressBar, percentProgress, array, canvas);
        });
        changeP.setOnAction(e -> {
            handleChangeP(table, array, canvas);
        });
        loadcsv.setOnAction(event -> {
            handleLoadCsv(stage, array, canvas, progressBar, percentProgress, cancel, loadcsv, generate, merge, add, editable, delete, nextTicket, changeP, table);
        });
        savecsv.setOnAction(event -> {
            handleSaveCsv(stage, editable, changeP, table);
        });
        generate.setOnAction(e -> {
            handleGenerateTask(generateText, progressBar, percentProgress, array, canvas, cancel, generate, loadcsv, merge, add, editable, delete, nextTicket, changeP, table);
        });
        getU.setOnAction(event -> {
            handleUrgent(mostK);
        });
        search.setOnAction((event -> {
            handleSearch(searchText, canvas);
        }));
        delete.setOnAction((event -> {
            handleDelete(table, array, canvas);
        }));
        nextTicket.setOnAction(e -> handleNext(array, canvas));
        add.setOnAction(event -> {
            handleAdd(array, canvas);
        });
        editable.setOnAction(event -> {
            handleEdit(table, array, canvas);
        });
        Scene scene = new Scene(borderPane, 1200, 800);
        stage.setTitle("Patients Tickets");
        stage.setScene(scene);
        stage.show();
    }

    private void handleSearch(TextField searchText, HeapVisualizer canvas) {
        String text = searchText.getText().trim();
        try {
            int num;
            num = Integer.parseInt(text);
            int lastindex = binHeap.search((ticket) -> {
                if (ticket.getCardNumber() == num) {
                    return true;
                }
                return false;
            });
            canvas.redraw(binHeap);
            if (lastindex == -1) {
                Alert alert = errAlert("Search", null, "Tickets not found");
                alert.showAndWait();
            } else {
                Alert alert = infoAlertTicket("Search result", null, binHeap.get(lastindex), "Search Ticket: ");
                alert.showAndWait();
            }


        } catch (NumberFormatException e) {
            Alert alert = errAlert("Error", e.getClass().toString(), e.getMessage());
            alert.showAndWait();
        }
    }

    private void handleChangeP(TableView<Ticket> table, ObservableList<Ticket> array, HeapVisualizer canvas) {
        Ticket ticket = table.getSelectionModel().getSelectedItem();
        if (ticket != null) {
            Dialog<ButtonType> dialog = new Dialog<>();
            ComboBox<Integer> prior = new ComboBox<>();
            prior.getItems().addAll(0, 1, 2, 3);
            prior.getSelectionModel().selectFirst();
            dialog.getDialogPane().setContent(prior);
            dialog.setTitle("Change Priority");
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            dialog.setWidth(400);
            dialog.setHeight(300);
            dialog.getDialogPane().setPadding(new Insets(50));
            Optional<ButtonType> showRes = dialog.showAndWait();
            if (showRes.isPresent() && showRes.get() == ButtonType.OK) {
                int pr = prior.getValue();
                int index = binHeap.search(ticket);
                ticket.setPriority(pr);
                binHeap.updatePosition(index);
                refreshView(array, canvas);
            }
        } else {
            errAlert("Item is null", null, "Selected item is null").showAndWait();
        }
    }

    private void handleDelete(TableView<Ticket> table, ObservableList<Ticket> array, HeapVisualizer canvas) {
        Ticket selected = table.getSelectionModel().getSelectedItem();
        try {
            if (selected == null) {
                throw new IllegalArgumentException("Field is null");
            }
            binHeap.remove(selected);
            Alert alert = infoAlertTicket("Delete Ticket", "Ticket was deleted", selected, "Deleted element: ");
            refreshView(array, canvas);
            alert.showAndWait();
        } catch (IllegalArgumentException e) {
            Alert alert = errAlert("Incorrect argument", null, e.getMessage());
            alert.showAndWait();
        }
    }

    private void handleNext(ObservableList<Ticket> array, HeapVisualizer canvas) {
        try {
            Ticket ticket = binHeap.pop();
            Alert alert = infoAlertTicket("Next Ticket", "The most urgent ticket", ticket, "Next Ticket: ");
            refreshView(array, canvas);
            alert.showAndWait();
        } catch (NoSuchElementException e) {
            Alert alert = errAlert("Heap is null", null, e.getMessage());
            alert.showAndWait();
        }
    }

    private void handleAdd(ObservableList<Ticket> array, HeapVisualizer canvas) {
        TicketForm form = editdialog("add Field");
        Optional<ButtonType> showres = form.dialog.showAndWait();
        if (showres.isPresent() && showres.get() == ButtonType.OK) {
            try {
                Ticket res = applyTicketForm(form);
                binHeap.add(res);
                refreshView(array, canvas);
            } catch (IllegalArgumentException | DateTimeParseException e) {
                Alert alert = errAlert("Incorrect argument", null, e.getMessage());
                alert.showAndWait();
            }
        }
    }

    private void handleUrgent(TextField mostK) {
        try {
            int num = Integer.parseInt(mostK.getText().trim());
            if (num <= 0 || num > binHeap.getSize()) {
                throw new IllegalArgumentException("Incorrect value");
            }
            Ticket[] tickets = new Ticket[num];
            BinHeap<Ticket> copy = binHeap.copy();
            copy.clearTrace();
            for (int i = 0; i < num; i++) {
                tickets[i] = copy.pop();
            }
            countUrgent = 0;
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Most urgent tickets");
            dialog.setHeaderText("Ticket information");
            Label ticketInfo = new Label();
            Button back = new Button("Back");
            Button next = new Button("Next");
            ButtonType exitType = new ButtonType("Exit", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().add(exitType);
            HBox buttons = new HBox(10, back, next);
            VBox content = new VBox(15, ticketInfo, buttons);
            dialog.getDialogPane().setContent(content);
            back.setDisable(true);
            next.setDisable(num == 1);
            ticketInfo.setText(getStringTicketInfo(tickets[countUrgent]));
            next.setOnAction(e -> {
                if (countUrgent < num - 1) {
                    countUrgent++;
                }

                back.setDisable(countUrgent == 0);
                next.setDisable(countUrgent == num - 1);

                ticketInfo.setText(getStringTicketInfo(tickets[countUrgent]));
            });

            back.setOnAction(e -> {
                if (countUrgent > 0) {
                    countUrgent--;
                }
                back.setDisable(countUrgent == 0);
                next.setDisable(countUrgent == num - 1);
                ticketInfo.setText(getStringTicketInfo(tickets[countUrgent]));
            });
            dialog.getDialogPane().setPrefSize(420, 330);
            content.setPadding(new Insets(20));
            dialog.showAndWait();
        } catch (IllegalArgumentException e) {
            Alert alert = errAlert(e.getClass().toString(), null, e.getMessage());
            alert.showAndWait();
        }
    }

    private void handleMerge(Button loadcsv, Button generate, Button merge, Button add, Button editable, Button delete, Button nextTicket, Button changeP, Button cancel, TableView<Ticket> table, Stage stage, ProgressBar progressBar, Label percentProgress, ObservableList<Ticket> array, HeapVisualizer canvas) {
        if (binHeap == null) {
            Alert alert = errAlert("Merge error", null, "Main queue is not created");
            alert.showAndWait();
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Csv Files", "*.csv"));
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) {
            return;
        }
        setMergeControls(true, loadcsv, generate, merge, add, editable, delete, nextTicket, changeP, cancel, table);
        Task<BinHeap<Ticket>> mergeTask = new Task<>() {
            @Override
            protected BinHeap<Ticket> call() throws Exception {
                CsvLoadResult res = CsvLoader.load(file.toPath(), getErrorLogPath(file.toPath()), (current, total) -> {
                    updateProgress(current * 9, total * 10);
                    return isCancelled();
                });
                if (isCancelled()) {
                    return null;
                }
                BinHeap<Ticket> secondHeap = res.getTickets();
                if (secondHeap == null) {
                    throw new IllegalArgumentException("Second queue is null");
                }
                BinHeap<Ticket> result = binHeap.merge(secondHeap);
                updateProgress(1, 1);
                return result;
            }
        };
        progressBar.progressProperty().bind(mergeTask.progressProperty());
        percentProgress.textProperty().bind(mergeTask.progressProperty().multiply(100).asString("%.2f%%"));
        mergeTask.setOnSucceeded(event -> {
            unBind(progressBar, percentProgress, "100%", 1);
            binHeap = mergeTask.getValue();
            binHeap.clearTrace();
            refreshView(array, canvas);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Merge");
            alert.setHeaderText("Queues merged successfully");
            alert.setContentText("Tickets after merge: " + binHeap.getSize());
            alert.show();
            setMergeControls(false, loadcsv, generate, merge, add, editable, delete, nextTicket, changeP, cancel, table);
        });
        mergeTask.setOnCancelled(event -> {
            unBind(progressBar, percentProgress, "canceled", 0);
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Canceled");
            alert.setHeaderText("Merge queues");
            alert.setContentText("Merge canceled");
            alert.show();
            setMergeControls(false, loadcsv, generate, merge, add, editable, delete, nextTicket, changeP, cancel, table);
        });
        mergeTask.setOnFailed(event -> {
            unBind(progressBar, percentProgress, "failed", 0);
            Throwable exception = mergeTask.getException();
            Alert alert = errAlert(exception.getClass().toString(), null, exception.getMessage());
            alert.showAndWait();
            setMergeControls(false, loadcsv, generate, merge, add, editable, delete, nextTicket, changeP, cancel, table);
        });

        cancel.setOnAction(event -> {
            mergeTask.cancel();
        });
        Thread thread = new Thread(mergeTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void handleEdit(TableView<Ticket> table, ObservableList<Ticket> array, HeapVisualizer canvas) {
        Ticket selected = table.getSelectionModel().getSelectedItem();
        try {
            if (selected == null) {
                throw new IllegalArgumentException("Field is null");
            }
            TicketForm form = editdialog("edit Field");
            form.comboType.setDisable(true);
            form.cardNum.setText(String.valueOf(selected.getCardNumber()));
            form.name.setText(selected.getName());
            form.office.setText(String.valueOf(selected.getOffice()));
            form.priority.getSelectionModel().select(selected.getPriority());
            form.time.setText(String.valueOf(selected.getTime()));
            if (selected instanceof HomeTicket home) {
                form.comboType.getSelectionModel().select(TicketType.HOME);
                form.address.setText(home.getAdres());
                form.address.setDisable(false);
            } else {
                form.comboType.getSelectionModel().select(TicketType.REGULAR);
            }
            Optional<ButtonType> showres = form.dialog.showAndWait();
            if (showres.isPresent() && showres.get() == ButtonType.OK) {
                Ticket ticket = applyTicketForm(form);
                if (getStringTicketInfo(selected).equals(getStringTicketInfo(ticket))) {
                    return;
                }
                int index = binHeap.search(selected);

                selected.setCardNumber(ticket.getCardNumber());
                selected.setName(ticket.getName());
                selected.setOffice(ticket.getOffice());
                selected.setPriority(ticket.getPriority());
                selected.setTime(ticket.getTime());

                if (selected instanceof HomeTicket oldHome &&
                        ticket instanceof HomeTicket newHome) {
                    oldHome.setAdres(newHome.getAdres());
                }

                binHeap.updatePosition(index);
                refreshView(array, canvas);
            }
        } catch (IllegalArgumentException | DateTimeParseException | NoSuchElementException e) {
            Alert alert = errAlert("Incorrect argument", null, e.getMessage());
            alert.showAndWait();
        }
    }

    private void handleGenerate(TextField generate, ObservableList<Ticket> array, HeapVisualizer canvas, Button cancel, Button generateButton) {
        try {
            setButtonDisable(generateButton, true, cancel, false);
            int count = Integer.parseInt(generate.getText().trim());
            BinHeap<Ticket> ticketBinHeap = createHeap();
            TicketGenerator ticketGenerator = new TicketGenerator();
            ticketGenerator.generate(ticketBinHeap, count);
            binHeap = ticketBinHeap;
            refreshView(array, canvas);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Generation");
            alert.setHeaderText("Generation tickets");
            double ms = ticketGenerator.getMs();
            alert.setContentText("Generated tickets: " + count + "\nIn " + String.format("%.2f", ms) + " milliseconds");
            alert.showAndWait();
            setButtonDisable(generateButton, false, cancel, true);
        } catch (IllegalArgumentException | IOException e) {
            Alert alert = errAlert(e.getClass().toString(), null, e.getMessage());
            alert.showAndWait();
        }
    }

    private void handleGenerateTask(TextField generateText, ProgressBar progressBar, Label percentProgress, ObservableList<Ticket> array, HeapVisualizer canvas, Button cancel, Button generateButton, Button load, Button merge, Button add, Button editable, Button delete, Button nextTicket, Button changeP, TableView<Ticket> table) {
        try {

            setMergeControls(true, load, generateButton, merge, add, editable, delete, nextTicket, changeP, cancel, table);
            int count = Integer.parseInt(generateText.getText());
            TicketGeneratorTask ticketGeneratorTask = new TicketGeneratorTask(new TicketGenerator(), createHeap(), count);
            Thread thread = new Thread(ticketGeneratorTask);
            progressBar.progressProperty().bind(ticketGeneratorTask.progressProperty());
            percentProgress.textProperty().bind(ticketGeneratorTask.progressProperty().multiply(100).asString("%.2f%%"));
            ticketGeneratorTask.setOnCancelled((event) -> {
                unBind(progressBar, percentProgress, "canceled", 0f);
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Canceled");
                alert.setHeaderText("Generation tickets");
                alert.setContentText("Generated Canceled");
                alert.show();
                setMergeControls(false, load, generateButton, merge, add, editable, delete, nextTicket, changeP, cancel, table);
            });
            ticketGeneratorTask.setOnFailed((event) -> {
                unBind(progressBar, percentProgress, "failed", 0f);
                Throwable exception = ticketGeneratorTask.getException();
                Alert alert = errAlert(exception.getClass().toString(), null, exception.getMessage());
                alert.showAndWait();
                setMergeControls(false, load, generateButton, merge, add, editable, delete, nextTicket, changeP, cancel, table);
            });
            ticketGeneratorTask.setOnSucceeded((event) -> {
                unBind(progressBar, percentProgress, "100%", 1);
                binHeap = ticketGeneratorTask.getValue();
                refreshView(array, canvas);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Generation");
                alert.setHeaderText("Generation tickets");
                double ms = ticketGeneratorTask.getMs();
                alert.setContentText("Generated tickets: " + count + "\nIn " + String.format("%.2f", ms) + " milliseconds");
                alert.show();
                setMergeControls(false, load, generateButton, merge, add, editable, delete, nextTicket, changeP, cancel, table);
            });

            cancel.setOnAction((event) -> {
                ticketGeneratorTask.cancel();
            });
            thread.setDaemon(true);
            thread.start();
        } catch (IllegalArgumentException | IOException ex) {
            setMergeControls(false, load, generateButton, merge, add, editable, delete, nextTicket, changeP, cancel, table);
            Alert alert = errAlert(ex.getClass().toString(), null, ex.getMessage());
            alert.showAndWait();
        }
    }

    private void setButtonDisable(Button generateButton, boolean one, Button cancel, boolean two) {
        generateButton.setDisable(one);
        cancel.setDisable(two);
    }

    private void handleLoadCsv(Stage stage, ObservableList<Ticket> array, HeapVisualizer canvas, ProgressBar progressBar, Label percentProgress, Button cancel, Button loadCsv, Button generate, Button merge, Button add, Button editable, Button delete, Button nextTicket, Button changeP, TableView<Ticket> table) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Csv Files", "*.csv"));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            setMergeControls(true, loadCsv, generate, merge, add, editable, delete, nextTicket, changeP, cancel, table);
            Task<CsvLoadResult> load = new Task<>() {
                @Override
                protected CsvLoadResult call() throws Exception {

                    CsvLoadResult res = CsvLoader.load(file.toPath(), getErrorLogPath(file.toPath()), (current, total) -> {
                        updateProgress(current, total);
                        return isCancelled();
                    });

                    return res;
                }
            };
            progressBar.progressProperty().bind(load.progressProperty());
            percentProgress.textProperty().bind(load.progressProperty().multiply(100).asString("%.2f%%"));
            Thread thread = new Thread(load);
            load.setOnSucceeded(e -> {
                unBind(progressBar, percentProgress, "100%", 1f);
                CsvLoadResult res = load.getValue();
                binHeap = res.getTickets();
                refreshView(array, canvas);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Load information");
                alert.setHeaderText("Successful loaded: " + res.getTickets().getSize());
                if (res.getErrorCount() == 0) {
                    alert.setContentText("No errors");
                } else {
                    alert.setContentText("Errors: " + res.getErrorCount() + "\nError log saved to:\n" + getErrorLogPath(file.toPath()));
                }
                setMergeControls(false, loadCsv, generate, merge, add, editable, delete, nextTicket, changeP, cancel, table);
                alert.show();
            });
            load.setOnCancelled((e) -> {
                unBind(progressBar, percentProgress, "canceled", 0f);
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Canceled");
                alert.setHeaderText("Load tickets");
                alert.setContentText("Loading Canceled");
                setMergeControls(false, loadCsv, generate, merge, add, editable, delete, nextTicket, changeP, cancel, table);
                alert.show();
            });
            load.setOnFailed(e -> {
                unBind(progressBar, percentProgress, "failed", 0f);
                Throwable exception = load.getException();
                Alert alert = errAlert("Error IO", null, exception.getMessage());
                setMergeControls(false, loadCsv, generate, merge, add, editable, delete, nextTicket, changeP, cancel, table);
                alert.showAndWait();
            });
            cancel.setOnAction((event) -> {
                load.cancel();
            });
            thread.setDaemon(true);
            thread.start();
        }
    }

    private Path getErrorLogPath(Path csvPath) {
        String fileName = csvPath.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            fileName = fileName.substring(0, dotIndex);
        }
        return csvPath.resolveSibling(fileName + "_errors.txt");
    }

    private void handleSaveCsv(Stage stage, Button edit, Button changeP, TableView<Ticket> table) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV");
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            activeSaves++;
            edit.setDisable(true);
            changeP.setDisable(true);
            BinHeap<Ticket> snapshot = binHeap.copy();
            Task<Void> save = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    CsvSaver.save(snapshot, file.toPath());
                    return null;
                }
            };
            save.setOnSucceeded((e) -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Save successfully");
                alert.setHeaderText(null);
                alert.setContentText("File save successfully");
                alert.show();
                activeSaves--;
                restoreEditButtons(table, edit, changeP);
            });
            save.setOnFailed((ev) -> {
                Throwable e = save.getException();
                Alert al = errAlert(e.getClass().toString(), null, e.getMessage());
                al.showAndWait();
                activeSaves--;
                restoreEditButtons(table, edit, changeP);
            });
            save.setOnCancelled(e -> {
                activeSaves--;
                restoreEditButtons(table, edit, changeP);
            });
            Thread thread = new Thread(save);
            thread.setDaemon(true);
            thread.start();
        }
    }

    private void restoreEditButtons(TableView<Ticket> table, Button edit, Button changeP) {
        Ticket selectedTicket = table.getSelectionModel().getSelectedItem();

        if (selectedTicket instanceof Editable && !heapWriteLocked && activeSaves == 0) {
            edit.setDisable(false);
            changeP.setDisable(false);
        } else {
            edit.setDisable(true);
            changeP.setDisable(true);
        }
    }

    private void unBind(ProgressBar progressBar, Label percentProgress, String text, double progress) {
        progressBar.progressProperty().unbind();
        progressBar.setProgress(progress);
        percentProgress.textProperty().unbind();
        percentProgress.setText(text);
    }

    private void refreshView(ObservableList<Ticket> array, HeapVisualizer canvas) {
        refreshTable(array);
        canvas.redraw(binHeap);
    }

    private void listenerTableProperty(TableView<Ticket> table, Button delete, Button editable, Button changeP) {
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldTicket, newTicket) -> {
            if (heapWriteLocked) {
                delete.setDisable(true);
                editable.setDisable(true);
                changeP.setDisable(true);
                return;
            }
            if (newTicket != null) {
                delete.setDisable(false);
            } else {
                delete.setDisable(true);
            }

            restoreEditButtons(table, editable, changeP);
        });
    }

    private Alert errAlert(String title, String header, String context) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(context);
        return alert;
    }

    private Alert infoAlertTicket(String title, String header, Ticket ticket, String context) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(context + getStringTicketInfo(ticket));
        return alert;
    }

    private void refreshTable(ObservableList<Ticket> array) {
        array.clear();
        int size = Math.min(binHeap.getSize(), MAX_TABLE_ITEMS);
        for (int i = 0; i < size; i++) {
            array.add(binHeap.get(i));
        }
    }

    private void setMergeControls(boolean running, Button loadcsv, Button generate, Button merge, Button add, Button editable, Button delete, Button nextTicket, Button changeP, Button cancel, TableView<Ticket> table) {
        heapWriteLocked = running;
        if (running) {
            loadcsv.setDisable(true);
            generate.setDisable(true);
            merge.setDisable(true);
            add.setDisable(true);
            editable.setDisable(true);
            delete.setDisable(true);
            nextTicket.setDisable(true);
            changeP.setDisable(true);
            cancel.setDisable(false);
        } else {
            loadcsv.setDisable(false);
            generate.setDisable(false);
            merge.setDisable(false);
            add.setDisable(false);
            cancel.setDisable(true);
            nextTicket.setDisable(binHeap == null || binHeap.getSize() == 0);
            Ticket selected = table.getSelectionModel().getSelectedItem();
            delete.setDisable(selected == null);
            restoreEditButtons(table, editable, changeP);
        }
    }

    private String getStringTicketInfo(Ticket ticket) {
        String info = "\nType: " + ticket.getClass().getSimpleName() + "\nCard Number: " + ticket.getCardNumber() + "\nName: " + ticket.getName() + "\nOffice: " + ticket.getOffice() + "\nPriority: " + ticket.getPriority() + "\nTime: " + ticket.getTime().toString();
        if (ticket instanceof HomeTicket homeTicket) {
            info += "\nAddress: " + homeTicket.getAdres();
        }
        return info;
    }

    private TicketForm editdialog(String title) {
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

    private BinHeap<Ticket> createHeap() {
        return new BinHeap<>((ticket1, ticket2) -> {
            int res = Integer.compare(ticket1.getPriority(), ticket2.getPriority());
            if (res == 0) {
                res = ticket1.getTime().compareTo(ticket2.getTime());
            }
            return res;
        });
    }

    private Ticket applyTicketForm(TicketForm form) {
        TicketType typed = form.comboType.getValue();
        int cardNumber = Integer.parseInt(form.cardNum.getText());
        String addname = form.name.getText();
        int off = Integer.parseInt(form.office.getText());
        int priOrity = form.priority.getValue();
        LocalTime tme = LocalTime.parse(form.time.getText());
        if (addname.isBlank() || addname.contains(";")) {
            throw new IllegalArgumentException("Incorrect field name");
        }
        if (typed == TicketType.REGULAR) {
            return new RegularTicket(cardNumber, off, addname, priOrity, tme);
        } else {
            String adress = form.address.getText();
            if (adress.isBlank() || adress.contains(";")) {
                throw new IllegalArgumentException("Incorrect field address");
            }
            return new HomeTicket(cardNumber, off, addname, priOrity, tme, adress);
        }
    }

}



