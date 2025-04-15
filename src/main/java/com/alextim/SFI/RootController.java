package com.alextim.SFI;

import com.alextim.SFI.frontend.MainWindow;
import com.alextim.SFI.frontend.view.dynamic.DynamicController;
import com.alextim.SFI.frontend.view.param.ParamController;
import com.alextim.SFI.service.ExportService;
import com.alextim.SFI.service.PhotonMeasSystemService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;


@Slf4j
public class RootController extends RootControllerInitializer {

    public RootController(MainWindow mainWindow,
                          PhotonMeasSystemService photonMeasSystemService,
                          ExportService exportService) {
        super(mainWindow, photonMeasSystemService, exportService);
    }

    public void close() {
        log.info("close");

        ((DynamicController) getChild(DynamicController.class.getSimpleName())).stop();
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException ex) {
            executorService.shutdownNow();
        }
        log.info("executorService shutdown OK");

        scheduledExecutorService.shutdown();
        try {
            if (!scheduledExecutorService.awaitTermination(1, TimeUnit.SECONDS)) {
                scheduledExecutorService.shutdownNow();
            }
        } catch (InterruptedException ex) {
            scheduledExecutorService.shutdownNow();
        }
        log.info("scheduledExecutorService shutdown OK");


        ((ParamController) getChild(ParamController.class.getSimpleName())).writeSettingsToFile();
        log.info("Setting write to file OK");

        log.info("close OK");
    }
}
