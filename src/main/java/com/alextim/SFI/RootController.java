package com.alextim.SFI;

import com.alextim.SFI.frontend.MainWindow;
import com.alextim.SFI.frontend.view.dynamic.DynamicController;
import com.alextim.SFI.frontend.view.param.ParamController;
import com.alextim.SFI.service.ExportService;
import com.alextim.SFI.service.PhotonMeasSystemService;
import lombok.extern.slf4j.Slf4j;


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
        log.info("DynamicController deinitialize OK");

        scheduledExecutorService.shutdown();
        log.info("StaticController deinitialize OK");

        ((ParamController) getChild(ParamController.class.getSimpleName())).writeSettingsToFile();
        log.info("Setting write to file OK");

        log.info("close OK");
    }
}
