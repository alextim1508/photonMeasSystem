package com.alextim.SFI.context;


import com.alextim.SFI.RootController;
import com.alextim.SFI.frontend.MainWindow;
import com.alextim.SFI.service.MkoService;
import com.alextim.SFI.service.ExportService;
import com.alextim.SFI.service.PhotonMeasSystemService;
import com.alextim.SFI.transfer.MMSPTransfer;
import com.alextim.SFI.transfer.MMSPLibrary;
import com.sun.jna.Native;
import lombok.Cleanup;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Properties;

@Slf4j
public class Context {

    @Getter
    RootController rootController;


    private MMSPTransfer mmspTransfer;
    private PhotonMeasSystemService photonMeasSystemService;
    private ExportService exportService;

    public Context(MainWindow mainWindow, String[] args) {
        handleCommandArgs(args);

        readAppProperty();

        createBeans(mainWindow);
    }

    void handleCommandArgs(String[] args) {
//        IS_EXPERT = false;
//
//        if (args != null) {
//            for (String arg : args) {
//                log.info("Arg command line: {}", arg);
//                if (arg.equalsIgnoreCase("expert")) {
//                    IS_EXPERT = true;
//                }
//            }
//        }

        IS_EXPERT = true;

        log.info(IS_EXPERT ? "It is expert!" : "It is not expert!");
    }

    @SneakyThrows
    void readAppProperty() {
        Properties properties = new Properties();
        try {
            @Cleanup Reader reader = new BufferedReader(new FileReader(
                    System.getProperty("user.dir") + "/config/application.properties"));

            log.info("File application.properties from currently dir is found!");
            properties.load(reader);
        } catch (Exception e) {
            log.info("There are default properties!");

            @Cleanup InputStream resourceAsStream = Context.class.getClassLoader()
                    .getResourceAsStream("application.properties");
            @Cleanup Reader resourceReader = new InputStreamReader(resourceAsStream, "UTF-8");
            properties.load(resourceReader);
        }

        appPropertiesInit(properties);
    }

    private void appPropertiesInit(Properties properties) {
        TITLE_APP = (String) properties.get("app.title");
        log.info("TITLE_APP: {}", TITLE_APP);
    }

    void createBeans(MainWindow mainWindow) {
        createTransfer();
        createRepositories();
        createServices();
        createRootController(mainWindow);
    }

    private void createTransfer() {
        MMSPLibrary library = Native.load("drtl3_64.dll", MMSPLibrary.class);
        mmspTransfer = new MMSPTransfer(library);
    }

    private void createRepositories() {
    }

    private void createServices() {
        MkoService channelService = new MkoService(mmspTransfer);

        photonMeasSystemService = new PhotonMeasSystemService(channelService);

        exportService = new ExportService();
    }

    private void createRootController(MainWindow mainWindow) {
        log.info("Creating root controller");
        rootController = new RootController(mainWindow, photonMeasSystemService, exportService);
    }

    public static String TITLE_APP;
    public static Boolean IS_EXPERT;
}
