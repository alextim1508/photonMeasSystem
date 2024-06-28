package com.alextim.SFI;

import com.alextim.SFI.frontend.MainWindow;
import com.alextim.SFI.frontend.view.NodeController;
import com.alextim.SFI.service.PhotonMeasSystemService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
@RequiredArgsConstructor
@Slf4j
public class RootControllerInitializer {


    private final PhotonMeasSystemService photonMeasSystemService;
    private final MainWindow mainWindow;

    protected ExecutorService executorService = Executors.newFixedThreadPool(4);
    protected Map<String, NodeController> children = new HashMap<>();

    public void addChild(String name, NodeController child) {
        log.debug("{} is added to root controller children", name);
        children.put(name, child);
    }

    public NodeController getChild(String name) {
        return children.get(name);
    }
}
