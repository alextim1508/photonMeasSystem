package com.alextim.SFI.frontend.view.work;

import com.alextim.SFI.frontend.view.NodeController;
import com.alextim.SFI.frontend.view.param.ParamController;
import com.alextim.SFI.frontend.view.param.ParamController.Setting;
import com.alextim.SFI.service.MeasResult;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static com.alextim.SFI.frontend.MainWindow.PROGRESS_BAR_COLOR;
import static com.alextim.SFI.service.Converter.convert;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
public class WorkController extends NodeController {

    @FXML
    private TextField measTime;

    @FXML
    private Label curHeight, aveHeight;

    @FXML
    private Button startBtn, stopBtn;

    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label progressLabel;

    @AllArgsConstructor
    @NoArgsConstructor
    public static class WorkMeasResult {
        public long curHeight;
        public double aveHeight;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        progressBar.setStyle("-fx-accent: " + PROGRESS_BAR_COLOR);
    }

    @Override
    protected String getName() {
        return getClass().getSimpleName();
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
                    "Ошибка разбора поля",
                    "Ошибка преобразования строки из поля Время измерения в число ");
            return;
        }


        Setting setting = ((ParamController) rootController.getChild(ParamController.class.getSimpleName())).getSetting();
        log.info("Setting: {}", setting);

        WorkMeasResult workMeasResult = new WorkMeasResult();

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

                        handleMeas(workMeasResult, measResult, count);

                        double progress = 1.0 * count / measTime;
                        Platform.runLater(() -> {
                            showHeights(workMeasResult);
                            setProgress(progress);
                        });
                    }, new AtomicBoolean(false), 1);

                    if (count == measTime) {
                        disableBtn(false);
                        log.info("========== startOn === OK ==========");
                    } else {
                        futureTask = rootController.getScheduledExecutorService().schedule(this, 1, SECONDS);
                    }
                } catch (Exception e) {
                    log.error("", e);
                    Platform.runLater(() -> mainWindow.showError(Thread.currentThread(), e));
                    disableBtn(false);
                }
            }
        };
        rootController.getScheduledExecutorService().schedule(task, 0, MILLISECONDS);

    }

    private void showHeights(WorkMeasResult workMeasResult) {
        curHeight.setText(String.format("%d", workMeasResult.curHeight));
        aveHeight.setText(String.format("%.2f", workMeasResult.aveHeight));
    }

    private void clearHeights() {
        curHeight.setText("-");
        aveHeight.setText("-");
    }

    private void handleMeas(WorkMeasResult workMeasResult, MeasResult measResult, long count) {
        log.info("handleMeas");

        float coff = 1 - 1.0f / count;
        log.info("coef: {}", coff);

        workMeasResult.curHeight = measResult.height;
        log.info("curHeight: {}", workMeasResult.curHeight);

        workMeasResult.aveHeight = coff * workMeasResult.aveHeight + (1 - coff) * measResult.height;
        log.info("aveHeight: {}", workMeasResult.aveHeight);
    }


    @FXML
    void stopOn(ActionEvent event) {
        disableBtn(false);

        if (futureTask != null) {
            futureTask.cancel(false);
            setProgress(0);
            clearHeights();
        }
    }

    private long getMeasTime() {
        return Long.parseLong(measTime.getText());
    }

    private void disableBtn(boolean res) {
        startBtn.setDisable(res);
        stopBtn.setDisable(!res);
    }
}
