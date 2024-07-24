package com.alextim.SFI.frontend.view.management;

import com.alextim.SFI.frontend.view.NodeController;
import com.alextim.SFI.frontend.view.param.ParamController;
import com.alextim.SFI.frontend.view.param.ParamController.Setting;
import com.alextim.SFI.service.PhotonMeasSystemService.CommandParams;
import com.alextim.SFI.service.PhotonMeasSystemService.Commands;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

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

    private void commandComboInit(){
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
        command.getSelectionModel().selectFirst();

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
        if(param1 != null && param2 == null) {
            param = param1.code;
        } else if(param1 != null) {
            param = (short) (param1.code + 0x100 * param2.code);
        } else {
            param = 0;
        }

        log.info("command: {}", value.code);
        log.info("param: {}", param);

        setCommandData(String.format("Команда: %d, параметр: %d", value.code, param));

        rootController.getPhotonMeasSystemService().command(setting, value.code, param);
    }
}
