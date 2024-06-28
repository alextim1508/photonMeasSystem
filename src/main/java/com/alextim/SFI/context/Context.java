package com.alextim.SFI.context;


import com.alextim.SFI.RootController;
import com.alextim.SFI.frontend.MainWindow;
import com.alextim.SFI.service.ExchangeChannelService;
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

    private static final String VERSION = "v1";

    private MMSPTransfer mmspTransfer;
    private PhotonMeasSystemService photonMeasSystemService;

    public Context(MainWindow mainWindow, String[] args) {
        if (args != null) {
            for (String arg : args) {
                log.info("Arg command line: {}", arg);
                if (arg.equalsIgnoreCase("expert")) {
                    IS_EXPERT = true;
                }
            }
        }

        log.info(IS_EXPERT ? "It is expert!" : "It is not expert!");

        readAppProperty();

        createBeans(mainWindow);
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
        TITLE_APP = (String) properties.get("app.title") + VERSION;
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
        ExchangeChannelService channelService = new ExchangeChannelService(mmspTransfer);

        photonMeasSystemService = new PhotonMeasSystemService(channelService);
    }

    private void createRootController(MainWindow mainWindow) {
        log.info("Creating root controller");

        rootController = new RootController(mainWindow, photonMeasSystemService);
    }


    public static String TITLE_APP;
    public static Boolean IS_EXPERT = false;
    public static String DATE_TIME_FORMAT_WITH_MILLI_SECONDS = "yyyy-MM-dd HH:mm:ss:SSS";
    public static String DATE_TIME_FORMAT_WITH_SECONDS = "yyyy-MM-dd HH:mm:ss";
    public static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";
    public static String DATE_FORMAT = "yyyy-MM-dd";
    public static String DATE_FORMAT_WITH_FIRST_DAYS = "dd.MM.yy";

    public static String CHECK_MARK = "✔";
}
