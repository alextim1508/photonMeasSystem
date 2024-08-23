package com.alextim.SFI.frontend.view.param;

import com.alextim.SFI.frontend.view.NodeController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static com.alextim.SFI.context.Context.IS_EXPERT;

@Slf4j
public class ParamController extends NodeController {

    @Override
    protected String getName() {
        return getClass().getSimpleName();
    }

    @FXML
    private TextField addrMainChannel;
    @FXML
    private TextField delayLoop;
    @FXML
    private TextField frameTime;
    @FXML
    private TextField fronBufferSize;

    private final int DEFAULT_ADD_MAIN_CHANEL = 9;
    private final int DEFAULT_FRAME_TIME = 5;
    private final int DEFAULT_DELAY_LOOP = 10;
    private final int DEFAULT_FRON_BUF_SIZE = 5;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        readSettingsFromFile();

        if(!IS_EXPERT) {
            addrMainChannel.setEditable(false);
            delayLoop.setEditable(false);
            frameTime.setEditable(false);
            fronBufferSize.setEditable(false);
        }
    }

    private void readSettingsFromFile() {
        Setting setting = null;
        File settingFile = new File(System.getProperty("user.dir") + "/LastSetting.txt");
        if (settingFile.exists()) {
            try {
                setting = rootController.getExportService().importSettingFromFile(settingFile);
            } catch (Exception e) {
                log.error("", e);
            }
        }
        log.info("setting from file: {}", setting);

        if (setting != null)
            setSettings(setting);
    }

    public void writeSettingsToFile() {
        File settingFile = new File(System.getProperty("user.dir") + "/LastSetting.txt");
        try {
            rootController.getExportService().exportToFile(getSetting(), settingFile);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public void setAddrMainChannel(byte arrMainChannel) {
        this.addrMainChannel.setText(Byte.toString(arrMainChannel));
    }

    public byte getAddrMainChannel() {
        return Byte.parseByte(addrMainChannel.getText());
    }

    public void setFrameTime(int frameTime) {
        this.frameTime.setText(Integer.toString(frameTime));
    }

    public int getFrameTime() {
        return Integer.parseInt(frameTime.getText());
    }

    public int getDelayLoop() {
        return Integer.parseInt(delayLoop.getText());
    }

    public void setDelayLoop(int delayLoop) {
        this.delayLoop.setText(Integer.toString(delayLoop));
    }

    public int getFronBufferSize() {
        return Integer.parseInt(fronBufferSize.getText());
    }

    public void setFronBufferSize(int fronBufferSize) {
        this.fronBufferSize.setText(Integer.toString(fronBufferSize));
    }

    @AllArgsConstructor
    @ToString
    public static class Setting {
        public byte addrMainChannel;
        public int delayLoop;
        public int frameTime;
        public int fronBufferSize;
    }

    public Setting getSetting() {
        byte arrMainChannel;
        try {
            arrMainChannel = getAddrMainChannel();
        } catch (Exception e) {
            arrMainChannel = DEFAULT_ADD_MAIN_CHANEL;
        }

        int frameTime;
        try {
            frameTime = getFrameTime();
        } catch (Exception e) {
            frameTime = DEFAULT_FRAME_TIME;
        }

        int delayLoop;
        try {
            delayLoop = getDelayLoop();
        } catch (Exception e) {
            delayLoop = DEFAULT_DELAY_LOOP;
        }

        int fronBufferSize;
        try {
            fronBufferSize = getFronBufferSize();
        } catch (Exception e) {
            fronBufferSize = DEFAULT_FRON_BUF_SIZE;
        }

        return new Setting(arrMainChannel, delayLoop, frameTime, fronBufferSize);
    }

    public void setSettings(Setting settings) {
        setAddrMainChannel(settings.addrMainChannel);
        setDelayLoop(settings.delayLoop);
        setFrameTime(settings.frameTime);
        setFronBufferSize(settings.fronBufferSize);
    }
}
