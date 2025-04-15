package com.alextim.SFI.frontend.view.management;

import com.alextim.SFI.frontend.view.NodeController;
import com.alextim.SFI.frontend.view.param.ParamController;
import com.alextim.SFI.frontend.view.param.ParamController.Setting;
import com.alextim.SFI.service.MeasResult;
import com.alextim.SFI.service.MsgStatus;
import com.alextim.SFI.service.PhotonMeasSystemService.CommandParams;
import com.alextim.SFI.service.PhotonMeasSystemService.Commands;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.alextim.SFI.service.Converter.convert;
import static com.alextim.SFI.service.PhotonMeasSystemService.TechnologyCommands.CLOSE_TRANSMITTER;
import static com.alextim.SFI.service.PhotonMeasSystemService.TechnologyCommands.OPEN_TRANSMITTER;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
public class ManagementController extends NodeController {

    @FXML
    private TextField commandData;
    @FXML
    private ComboBox<CommandParams> param1, param2;
    @FXML
    private ComboBox<Commands> command;

    @Override
    protected String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        commandComboInit();
    }

    private void commandComboInit() {
        command.setItems(FXCollections.observableArrayList(Commands.values()));
        command.setConverter(new StringConverter<>() {
            @Override
            public String toString(Commands command) {
                if (command != null)
                    return command.title;
                return "";
            }

            @Override
            public Commands fromString(String string) {
                return command.getItems().stream().filter(ap ->
                        ap.title.equals(string)).findFirst().orElse(null);
            }
        });
        command.getSelectionModel().select(0);

        param1.setConverter(new StringConverter<>() {
            @Override
            public String toString(CommandParams command) {
                if (command != null)
                    return command.title;
                return "";
            }

            @Override
            public CommandParams fromString(String string) {
                return param1.getItems().stream().filter(ap ->
                        ap.title.equals(string)).findFirst().orElse(null);
            }
        });
        param2.setConverter(new StringConverter<>() {
            @Override
            public String toString(CommandParams command) {
                if (command != null)
                    return command.title;
                return "";
            }

            @Override
            public CommandParams fromString(String string) {
                return param2.getItems().stream().filter(ap ->
                        ap.title.equals(string)).findFirst().orElse(null);
            }
        });
    }

    @FXML
    void commandActionOn(ActionEvent event) {
        Commands value = command.getValue();
        param1.setItems(FXCollections.observableArrayList(value.params1));
        param1.getSelectionModel().selectFirst();
        param2.setItems(FXCollections.observableArrayList(value.params2));
        param2.getSelectionModel().selectFirst();
    }

    private void setCommandData(String commandData) {
        this.commandData.setText(commandData);
    }

    @FXML
    void sendCommandOn(ActionEvent event) {
        log.info("Sending command");

        Setting setting = ((ParamController) rootController.getChild(ParamController.class.getSimpleName())).getSetting();
        log.info("Setting: {}", setting);

        Commands value = command.getValue();
        log.info("command: {}", value);
        CommandParams param1 = this.param1.getValue();
        log.info("param1: {}", param1);
        CommandParams param2 = this.param2.getValue();
        log.info("param2: {}", param2);

        short param;
        if (param1 != null && param2 == null) {
            param = param1.code;
        } else if (param1 != null) {
            param = (short) (param1.code + 0x100 * param2.code);
        } else {
            param = 0;
        }

        log.info("command: {}", value.code);
        log.info("param: {}", param);

        setCommandData(String.format("Команда: %d, параметр: %d", value.code, param));

        rootController.getExecutorService().submit(() -> {
            try {
                rootController.getPhotonMeasSystemService()
                        .command(setting, value.code, param);
            } catch (Exception e) {
                log.error("", e);
                Platform.runLater(() -> mainWindow.showError(Thread.currentThread(), e));
            }
        });
    }

    private ScheduledFuture<?> futureTask;

    @FXML
    void openOn(ActionEvent event) {
        log.info("Sending open transmitter command");

        Setting setting = ((ParamController) rootController.getChild(ParamController.class.getSimpleName())).getSetting();
        log.info("Setting: {}", setting);

        setCommandData(String.format("Команда: %d", OPEN_TRANSMITTER.code));

        rootController.getExecutorService().submit(() -> {
            try {
                rootController.getPhotonMeasSystemService()
                        .sendTechnologyCommand(setting, OPEN_TRANSMITTER.code, (short) 0);
            } catch (Exception e) {
                log.error("Open detector", e);
                Platform.runLater(() -> mainWindow.showError(Thread.currentThread(), e));
            }
        });

        AtomicInteger attempt = new AtomicInteger(0);

        Runnable task = new Runnable() {
            @Override
            public void run() {
                AtomicBoolean isOpened = new AtomicBoolean(false);
                try {
                    rootController.getPhotonMeasSystemService().readMeasResults(setting, data -> {
                        MeasResult measResult = convert(data);
                        log.info("MeasResult: {}", measResult);

                        isOpened.set(isOpened(measResult));

                    }, new AtomicBoolean(false), 1);

                    if (isOpened.get()) {
                        Platform.runLater(() -> mainWindow.showDialog(Alert.AlertType.INFORMATION,
                                "Информация от передатчика СФИ",
                                "Команда открыть передатчик",
                                "Команда выполнена, передатчик открыт"));

                        log.info("========== startOn === OK ==========");
                    } else {
                        if(attempt.getAndIncrement() < 10) {
                            futureTask = rootController.getScheduledExecutorService().schedule(this, 1, SECONDS);
                        } else {
                            Platform.runLater(() -> mainWindow.showDialog(Alert.AlertType.INFORMATION,
                                    "Информация от передатчка СФИ",
                                    "Команда открыть передатчк",
                                    "Команда не выполнена"));

                            log.info("========== startOn === !OK ==========");
                        }
                    }
                } catch (Exception e) {
                    log.error("", e);
                    Platform.runLater(() -> mainWindow.showError(Thread.currentThread(), e));
                }
            }
        };
        rootController.getScheduledExecutorService().schedule(task, 1, SECONDS);
    }

    private boolean isOpened(MeasResult measResult) {
        log.info("PRD_STATUS = {}", measResult.status.get(MsgStatus.PRD_STATUS));
        log.info("PRD_ON = {}", measResult.status.get(MsgStatus.PRD_ON));

        boolean res = !measResult.status.get(MsgStatus.PRD_STATUS) && measResult.status.get(MsgStatus.PRD_ON);
        log.info("isOpened: {}", res);
        return res;
    }

    @FXML
    void closeOn(ActionEvent event) {
        log.info("Sending close transmitter command");

        Setting setting = ((ParamController) rootController.getChild(ParamController.class.getSimpleName())).getSetting();
        log.info("Setting: {}", setting);

        setCommandData(String.format("Команда: %d", CLOSE_TRANSMITTER.code));

        rootController.getExecutorService().submit(() -> {
            try {
                rootController.getPhotonMeasSystemService()
                        .sendTechnologyCommand(setting, CLOSE_TRANSMITTER.code, (short) 0);
            } catch (Exception e) {
                log.error("", e);
                Platform.runLater(() -> mainWindow.showError(Thread.currentThread(), e));
            }
        });

        AtomicInteger attempt = new AtomicInteger(0);

        Runnable task = new Runnable() {
            @Override
            public void run() {
                AtomicBoolean isClosed = new AtomicBoolean(false);
                try {
                    rootController.getPhotonMeasSystemService().readMeasResults(setting, data -> {
                        MeasResult measResult = convert(data);
                        log.info("MeasResult: {}", measResult);

                        isClosed.set(isClosed(measResult));

                    }, new AtomicBoolean(false), 1);

                    if (isClosed.get()) {
                        Platform.runLater(() -> mainWindow.showDialog(Alert.AlertType.INFORMATION,
                                "Информация от передатчка СФИ",
                                "Команда закрыть передатчк",
                                "Команда выполнена, передатчик закрыт"));

                        log.info("========== startOn === OK ==========");
                    } else {
                        if(attempt.getAndIncrement() < 10) {
                            futureTask = rootController.getScheduledExecutorService().schedule(this, 1, SECONDS);
                        } else {
                            Platform.runLater(() -> mainWindow.showDialog(Alert.AlertType.INFORMATION,
                                    "Информация от передатчка СФИ",
                                    "Команда закрыть передатчк",
                                    "Команда не выполнена"));

                            log.info("========== startOn === !OK ==========");
                        }
                    }
                } catch (Exception e) {
                    log.error("", e);
                    Platform.runLater(() -> mainWindow.showError(Thread.currentThread(), e));
                }
            }
        };
        rootController.getScheduledExecutorService().schedule(task, 1, SECONDS);
    }

    private boolean isClosed(MeasResult measResult) {
        log.info("PRD_STATUS = {}", measResult.status.get(MsgStatus.PRD_STATUS));
        log.info("PRD_ON = {}", measResult.status.get(MsgStatus.PRD_OFF));

        boolean res = !measResult.status.get(MsgStatus.PRD_STATUS) && measResult.status.get(MsgStatus.PRD_OFF);
        log.info("isClosed: {}", res);
        return res;
    }
}
