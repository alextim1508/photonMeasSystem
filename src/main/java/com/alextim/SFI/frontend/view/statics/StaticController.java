package com.alextim.SFI.frontend.view.statics;

import com.alextim.SFI.frontend.dialog.progress.ProgressDialog;
import com.alextim.SFI.frontend.view.NodeController;
import com.alextim.SFI.frontend.view.param.ParamController;
import com.alextim.SFI.service.MeasResult;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static com.alextim.SFI.frontend.MainWindow.PROGRESS_BAR_COLOR;
import static com.alextim.SFI.frontend.view.param.ParamController.*;
import static com.alextim.SFI.service.Converter.convert;

@Slf4j
public class StaticController extends NodeController {

    @AllArgsConstructor
    @NoArgsConstructor
    public static class StatMeasResult {
        public long refHeight;
        public double frequency1;
        public double frequency2;
        public double frequency3;
        public double frequency4;
    }

    @FXML
    private TextField serialNumber, machineNumber;
    @FXML
    private TextField refHeight;
    @FXML
    private TextField measTime;


    @FXML
    private Label progressLabel;
    @FXML
    private ProgressBar progressBar;


    @FXML
    private TextField currentMeas;

    @FXML
    private TableView<StatMeasResult> table;
    @FXML
    private TableColumn<StatMeasResult, Long> refHeightColumn;
    @FXML
    private TableColumn<StatMeasResult, Double> channel1Column;
    @FXML
    private TableColumn<StatMeasResult, Double> channel2Column;
    @FXML
    private TableColumn<StatMeasResult, Double> channel3Column;
    @FXML
    private TableColumn<StatMeasResult, Double> channel4Column;

    @FXML
    private Button startBtn , stopBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        progressBar.setStyle("-fx-accent: " + PROGRESS_BAR_COLOR);

        tableInitialize();
    }

    private void tableInitialize() {
        refHeightColumn.setCellValueFactory(param -> {
            return new ReadOnlyObjectWrapper<>(param.getValue().refHeight);
        });

        channel1Column.setCellValueFactory(param -> {
            return new ReadOnlyObjectWrapper<>(param.getValue().frequency1);
        });
        channel2Column.setCellValueFactory(param -> {
            return new ReadOnlyObjectWrapper<>(param.getValue().frequency2);
        });
        channel3Column.setCellValueFactory(param -> {
            return new ReadOnlyObjectWrapper<>(param.getValue().frequency3);
        });
        channel4Column.setCellValueFactory(param -> {
            return new ReadOnlyObjectWrapper<>(param.getValue().frequency4);
        });

        ContextMenu contextMenu = createContextMenu();
        Callback<TableView<StatMeasResult>, TableRow<StatMeasResult>> callback = param -> {
            TableRow<StatMeasResult> tableRow = new TableRow<>();
            tableRow.setContextMenu(contextMenu);
            return tableRow;
        };
        table.setRowFactory(callback);
        table.setItems(FXCollections.observableArrayList());
    }

    private ContextMenu createContextMenu() {
        MenuItem updateMenuItem = new MenuItem("Редактировать");
        updateMenuItem.setOnAction(event -> updateRowOn());
        MenuItem deleteMenuItem = new MenuItem("Удалить");
        deleteMenuItem.setOnAction(event -> deleteRowOn());
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(
                updateMenuItem,
                deleteMenuItem);
        return contextMenu;
    }

    private void updateRowOn() {
        StatMeasResult oldItem = table.getSelectionModel().getSelectedItem();
        StatMeasResult updatedItem = mainWindow.showStatMeasResultDialog(oldItem);
        if (updatedItem != null) {
            table.refresh();
        }
    }

    private void deleteRowOn() {
        ObservableList<StatMeasResult> selectedItems = table.getSelectionModel().getSelectedItems();
        table.getItems().removeAll(selectedItems);
    }

    public void setProgress(double progress) {
        progressBar.setProgress(progress);
        progressLabel.setText(String.format(Locale.US, " %.1f%%", 100 * progress));
    }

    private ScheduledFuture<?> futureTask;

    @FXML
    void startOn(ActionEvent event) {
        log.info("========== startOn ==========");
        long measTime;
        try {
            measTime = getMeasTime();
            log.info("measTime: {}", measTime);
        } catch (Exception e) {
            mainWindow.showDialog(Alert.AlertType.ERROR,
                    "Ошибка",
                    "Ошибка парсинга поля",
                    "Ошибка парсинга поля Время измерения");
            return;
        }

        long refHeight;
        try {
            refHeight = getRefHeight();
            log.info("refHeight: {}", refHeight);
        } catch (Exception e) {
            mainWindow.showDialog(Alert.AlertType.ERROR,
                    "Ошибка",
                    "Ошибка парсинга поля",
                    "Ошибка парсинга поля Высота");
            return;
        }

        Setting setting = ((ParamController) rootController.getChild(ParamController.class.getSimpleName())).getSetting();
        log.info("Setting: {}", setting);

        StatMeasResult statMeasResult = new StatMeasResult();
        statMeasResult.refHeight = refHeight;

        AtomicLong counter = new AtomicLong(0);

        disableBtn(true);

        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    long count = counter.incrementAndGet();
                    log.info("Count: {}", count);

                    rootController.getPhotonMeasSystemService().readMeasResults(setting, data -> {
                        MeasResult measResult = convert(data);
                        log.info("MeasResult: {}", measResult);

                        handleMeas(statMeasResult, measResult, count);

                        double progress = 1.0 * count / measTime;
                        Platform.runLater(() -> {
                            showMsg(measResult);
                            setProgress(progress);
                        });
                    }, new AtomicBoolean(false), 1);

                    if (count == measTime) {
                        Platform.runLater(() -> {
                            addTableRow(statMeasResult);
                            disableBtn(false);
                        });
                        log.info("========== startOn === OK ==========");
                    } else {
                        futureTask = rootController.getScheduledExecutorService().schedule(this, 1, TimeUnit.SECONDS);
                    }
                } catch (Exception e) {
                    log.error("", e);
                    Platform.runLater(() -> mainWindow.showError(Thread.currentThread(), e));
                }
            }
        };
        rootController.getScheduledExecutorService().schedule(task, 0, TimeUnit.MILLISECONDS);
    }

    private void addTableRow(StatMeasResult statMeasResult) {
        table.getItems().add(statMeasResult);
    }

    private void handleMeas(StatMeasResult statMeasResult, MeasResult measResult, long count) {
        log.info("handleMeas");

        float coff = 1 - 1.0f / count;
        log.info("coef: {}", coff);

        statMeasResult.frequency1 = coff * statMeasResult.frequency1 + (1 - coff) * measResult.frequency1;
        log.info("average frequency1: {}", statMeasResult.frequency1);

        statMeasResult.frequency2 = coff * statMeasResult.frequency2 + (1 - coff) * measResult.frequency2;
        log.info("average frequency2: {}", statMeasResult.frequency2);

        statMeasResult.frequency3 = coff * statMeasResult.frequency3 + (1 - coff) * measResult.frequency3;
        log.info("average frequency3: {}", statMeasResult.frequency3);

        statMeasResult.frequency4 = coff * statMeasResult.frequency4 + (1 - coff) * measResult.frequency4;
        log.info("average frequency4: {}", statMeasResult.frequency4);
    }

    @FXML
    void stopOn(ActionEvent event) {
        disableBtn(false);

        if(futureTask != null) {
            futureTask.cancel(false);
            setProgress(0);
            setCurrentMeas("");
        }
    }

    @FXML
    void exportOn(ActionEvent event) {
        exportMeasurements();
    }

    @Builder
    @ToString
    public static class StaticParam {
        public long prm1ch1Max, prm1ch1MaxFon, prm1ch1MinFon;
        public long prm1ch2Max, prm1ch2MaxFon, prm1ch2MinFon;
        public long prm2ch1Max, prm2ch1MaxFon, prm2ch1MinFon;
        public long prm2ch2Max, prm2ch2MaxFon, prm2ch2MinFon;
    }

    private void exportMeasurements() {
        ObservableList<StatMeasResult> measResults = table.getItems();
        if (measResults.isEmpty())
            return;

        StaticParam staticParam = null;
        File staticParamFile = new File(System.getProperty("user.dir") + "/LastStaticParam.txt");

        if(staticParamFile.exists()) {
            try {
                staticParam = rootController.getExportService().importStaticParamFromFile(staticParamFile);
            } catch (Exception e) {
                log.error("", e);
            }
        }
        log.info("staticParam from file: {}", staticParam);

        StaticParam newStaticParam = mainWindow.showStaticParamDialog(staticParam);
        if (newStaticParam == null)
            return;

        try {
            rootController.getExportService().exportToFile(newStaticParam, staticParamFile);
        }catch (Exception e) {
            log.error("", e);
        }

        File file = mainWindow.showFileChooseDialog();
        if (file == null)
            return;


        DoubleProperty progressProperty = new SimpleDoubleProperty(0.0);
        StringProperty statusProperty = new SimpleStringProperty("");

        final ProgressDialog progressDialog = mainWindow.showProgressDialog(progressProperty, statusProperty);

        rootController.getExecutorService().submit(() -> {
            log.info("export to selected file {}", file);

            rootController.getExportService().exportToFile(measResults, newStaticParam, serialNumber.getText(), machineNumber.getText(), file, (n, progress) ->
                    Platform.runLater(() -> {
                        progressProperty.set(progress);
                        statusProperty.set("Экспорт измерения " + n);
                    })
            );

            Platform.runLater(() -> {
                progressDialog.forcefullyHideDialog();

                mainWindow.showDialog(Alert.AlertType.INFORMATION,
                        "Экспорт",
                        "Измерение",
                        "Измерения экспортированы в файлы");
            });
        });
    }

    @Override
    protected String getName() {
        return getClass().getSimpleName();
    }

    private int getRefHeight() {
        return Integer.parseInt(refHeight.getText());
    }

    private long getMeasTime() {
        return Long.parseLong(measTime.getText());
    }

    private void showMsg(MeasResult measResult) {
        String MSG_FORMAT = "%d / %d / %d %d %d %d";
        setCurrentMeas(String.format(MSG_FORMAT,
                measResult.height,
                measResult.packetID,
                measResult.frequency1,
                measResult.frequency2,
                measResult.frequency3,
                measResult.frequency4
        ));
    }

    private void setCurrentMeas(String text) {
        currentMeas.setText(text);
    }

    private void disableBtn(boolean res) {
        startBtn.setDisable(res);
        stopBtn.setDisable(!res);
    }
}
