package com.alextim.SFI.frontend.view.dynamic;

import com.alextim.SFI.frontend.dialog.progress.ProgressDialog;
import com.alextim.SFI.frontend.view.NodeController;
import com.alextim.SFI.frontend.view.param.ParamController;
import com.alextim.SFI.frontend.view.param.ParamController.Setting;
import com.alextim.SFI.frontend.widget.SpectrumWidget;
import com.alextim.SFI.frontend.widget.graphs.SimpleGraph;
import com.alextim.SFI.service.MeasResult;
import com.alextim.SFI.service.MsgStatus;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.alextim.SFI.service.Converter.convert;


@Slf4j
public class DynamicController extends NodeController {

    @Override
    protected String getName() {
        return getClass().getSimpleName();
    }

    private SpectrumWidget spectrumWidget;
    private SimpleGraph simpleGraph;

    public Map<Long, MeasResult> measResults = new TreeMap<>();

    @AllArgsConstructor
    public static class MeasResultCell {
        public String id;
        public long value;
        public String comment;
    }

    @FXML
    private AnchorPane pane, graphPane;
    @FXML
    private TableView<MeasResultCell> paramTable;
    @FXML
    private TableColumn<MeasResultCell, String> paramId;
    @FXML
    private TableColumn<MeasResultCell, Long> paramValue;
    @FXML
    private TableColumn<MeasResultCell, String> paramComment;

    @FXML
    private TableView<MeasResultCell> flagTable;
    @FXML
    private TableColumn<MeasResultCell, String> flagId;
    @FXML
    private TableColumn<MeasResultCell, Long> flagValue;
    @FXML
    private TableColumn<MeasResultCell, String> flagComment;

    @FXML
    private Label stat;

    @FXML
    private Button startBtn , stopBtn;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        paramTableInitialize();
        flagTableInitialize();
        fullParamTable();
        fullFlagTable();

        initChart();
        addGraph();
    }

    private void initChart() {
        Platform.runLater(() -> {
            spectrumWidget = new SpectrumWidget(
                    "Высота", "см",
                    dataPoint -> {
                        handle((long) dataPoint.getX());
                    });
            AnchorPane spectrumPane = spectrumWidget.getPane();
            graphPane.getChildren().add(spectrumPane);
            AnchorPane.setTopAnchor(spectrumPane, 0.0);
            AnchorPane.setLeftAnchor(spectrumPane, 5.0);
            AnchorPane.setRightAnchor(spectrumPane, 5.0);
            AnchorPane.setBottomAnchor(spectrumPane, 5.0);
        });
    }

    private void addGraph() {
        Platform.runLater(() -> {
            simpleGraph = new SimpleGraph(new SimpleStringProperty("высоты"), new SimpleStringProperty(""));
            spectrumWidget.addGraph(simpleGraph);
        });
    }

    private void handle(long time) {
        MeasResult measResult = measResults.get(time);
        refreshTableRows(measResult);
    }

    private void paramTableInitialize() {
        paramId.setCellValueFactory(param -> {
            return new ReadOnlyObjectWrapper<>(param.getValue().id);
        });

        paramValue.setCellValueFactory(param -> {
            return new ReadOnlyObjectWrapper<>(param.getValue().value);
        });
        paramValue.setCellFactory(new Callback<>() {
            @Override
            public TableCell<MeasResultCell, Long> call(TableColumn<MeasResultCell, Long> column) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Long item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            if (item != null) {
                                if (this.getTableRow().getIndex() == 6) {
                                    setText(String.format("0x%08x", item));
                                } else {
                                    setText(String.format("%d", item));
                                }
                            }
                        }
                    }
                };
            }
        });


        paramComment.setCellValueFactory(param -> {
            return new ReadOnlyObjectWrapper<>(param.getValue().comment);
        });
        paramTable.setItems(FXCollections.observableArrayList());
    }

    private void flagTableInitialize() {
        flagId.setCellValueFactory(param -> {
            return new ReadOnlyObjectWrapper<>(param.getValue().id);
        });

        flagValue.setCellValueFactory(param -> {
            return new ReadOnlyObjectWrapper<>(param.getValue().value);
        });
        flagComment.setCellValueFactory(param -> {
            return new ReadOnlyObjectWrapper<>(param.getValue().comment);
        });
        flagTable.setItems(FXCollections.observableArrayList());
    }

    private void fullParamTable() {
        paramTable.getItems().add(new MeasResultCell("F1", 0, "Частота с PRM1-K1 (Гц)"));
        paramTable.getItems().add(new MeasResultCell("F2", 0, "Частота с PRM1-K2 (Гц)"));
        paramTable.getItems().add(new MeasResultCell("F3", 0, "Частота с PRM2-K1 (Гц)"));
        paramTable.getItems().add(new MeasResultCell("F4", 0, "Частота с PRM2-K2 (Гц)"));
        paramTable.getItems().add(new MeasResultCell("Height", 0, "Значение высоты (См)"));
        paramTable.getItems().add(new MeasResultCell("PacketId", 0, "Порядковый номер пакета"));
        paramTable.getItems().add(new MeasResultCell("Status", 0, "Статус"));
    }

    private void fullFlagTable() {
        for (MsgStatus status : MsgStatus.values()) {
            flagTable.getItems().add(new MeasResultCell(status.name(), 0, "-"));
        }
    }

    private void setStat(String stat) {
        this.stat.setText(stat);
    }

    private final LinkedList<MeasResult> queue = new LinkedList<>();

    @SneakyThrows
    public void addMeas(MeasResult measResult, int bufSize) {
        measResults.put(measResult.timestamp, measResult);
        queue.add(measResult);

        if (bufSize == 0) {
            refreshTableRows(measResult);
            addPoint(measResult);

        } else {
            if (queue.size() == bufSize) {
                simpleGraph.setAutoNotification(false);

                MeasResult lastRes = null;
                while (!queue.isEmpty()) {
                    lastRes = queue.poll();
                    addPoint(lastRes);
                }
                simpleGraph.setAutoNotification(true);

                refreshTableRows(lastRes);
            }
        }
    }

    private int ptnCount;

    public void addPoint(MeasResult measResult) {
        simpleGraph.addPoint(ptnCount++, measResult.timestamp, measResult.height);
    }

    private void refreshTableRows(MeasResult measResult) {
        ObservableList<MeasResultCell> items = paramTable.getItems();
        items.get(0).value = measResult.frequency1;
        items.get(1).value = measResult.frequency2;
        items.get(2).value = measResult.frequency3;
        items.get(3).value = measResult.frequency4;
        items.get(4).value = measResult.height;
        items.get(5).value = measResult.packetID;
        items.get(6).value = measResult.statusValue;

        for (int i = 0; i < MsgStatus.values().length; i++) {
            MsgStatus status = MsgStatus.values()[i];

            Boolean res = measResult.status.get(status);

            MeasResultCell cell = flagTable.getItems().get(i);
            if (res) {
                cell.value = 1;
                cell.comment = status.comment1;
            } else {
                cell.value = 0;
                cell.comment = status.comment0;
            }
        }

        paramTable.refresh();
        flagTable.refresh();
    }

    private final AtomicBoolean isStop = new AtomicBoolean();

    private Future<?> task;

    private int lastHashCode = -1;

    @SneakyThrows
    @FXML
    void startOn(ActionEvent event) {
        log.info("========== startOn ==========");
        disableBtn(true);


        Setting setting = ((ParamController) rootController.getChild(ParamController.class.getSimpleName())).getSetting();
        log.info("Setting: {}", setting);

        isStop.set(false);

        setStat("-");

        task = rootController.getExecutorService().submit(() -> {
            try {
                clearData();

                rootController.getPhotonMeasSystemService().readMeasResults(setting, data -> {
                    int hashCode = Arrays.hashCode(data);

                    if (hashCode != lastHashCode) {
                        lastHashCode = hashCode;

                        addMeas(convert(data), setting.fronBufferSize);
                    }
                }, isStop, Long.MAX_VALUE);

                String fails = getFailsInfo();
                log.info("fails: {}", fails);

                Platform.runLater(() -> setStat(fails));

                log.info("========== startOn === OK ==========");
            } catch (Exception e) {
                log.error("", e);
                Platform.runLater(() -> mainWindow.showError(Thread.currentThread(), e));
            }
        });
    }

    @SneakyThrows
    @FXML
    void stopOn(ActionEvent event) {
        disableBtn(false);
        stop();
    }

    public void stop() {
        isStop.set(true);
    }

    public String getFailsInfo() {
        List<Long> sortedTimestamps = measResults.keySet().stream().sorted().toList();

        long fails = 0;
        for (int j = 0; j < sortedTimestamps.size() - 1; j++) {
            Long cur = measResults.get(sortedTimestamps.get(j)).packetID;
            Long next = measResults.get(sortedTimestamps.get(j + 1)).packetID;

            if (Math.abs(cur - next) != 1)
                fails++;
        }
        long packetID1 = 0;
        long packetID2 = 0;
        if (!sortedTimestamps.isEmpty()) {
            packetID1 = measResults.get(sortedTimestamps.get(0)).packetID;
            packetID2 = measResults.get(sortedTimestamps.get(sortedTimestamps.size() - 1)).packetID;
        }

        return String.format("Потеряно сообщений: %d из %d", fails, (packetID2 - packetID1));
    }


    @SneakyThrows
    @FXML
    void exportOn(ActionEvent event) {
        exportMeasurements();
    }

    private void exportMeasurements() {
        File file = mainWindow.showFileChooseDialog();
        if (file != null) {
            if (measResults.isEmpty())
                return;

            DoubleProperty progressProperty = new SimpleDoubleProperty(0.0);
            StringProperty statusProperty = new SimpleStringProperty("");

            final ProgressDialog progressDialog = mainWindow.showProgressDialog(progressProperty, statusProperty);

            rootController.getExecutorService().submit(() -> {
                log.info("export to selected file {}", file);

                rootController.getExportService().exportToFile(measResults, file, (n, progress) ->
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
    }

    private void clearData() {
        measResults.clear();
        simpleGraph.clear();
        queue.clear();
        ptnCount = 0;

        Runtime.getRuntime().gc();
    }

    @SneakyThrows
    @FXML
    void clearOn(ActionEvent event) {
        clearData();
    }

    private void disableBtn(boolean res) {
        startBtn.setDisable(res);
        stopBtn.setDisable(!res);
    }
}