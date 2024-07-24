package com.alextim.SFI;

import com.alextim.SFI.frontend.MainWindow;
import com.alextim.SFI.frontend.view.NodeController;
import com.alextim.SFI.service.ExportService;
import com.alextim.SFI.service.PhotonMeasSystemService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

@Getter
@RequiredArgsConstructor
@Slf4j
public class RootControllerInitializer {

    protected final MainWindow mainWindow;
    protected final PhotonMeasSystemService photonMeasSystemService;
    protected final ExportService exportService;

    protected ExecutorService executorService = Executors.newFixedThreadPool(1);

    protected ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    protected Map<String, NodeController> children = new HashMap<>();

    public void addChild(String name, NodeController child) {
        log.debug("{} is added to root controller children", name);
        children.put(name, child);
    }

    public NodeController getChild(String name) {
        return children.get(name);
    }
}
