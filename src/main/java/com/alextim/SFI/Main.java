package com.alextim.SFI;

import com.alextim.SFI.context.Context;
import com.alextim.SFI.frontend.MainWindow;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main extends Application {
    private static String[] args;

    public static void main(String[] args) {
        Main.args = args;
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        log.info("start");
        System.setProperty("file.encoding", "UTF-8");

        MainWindow mainWindow = new MainWindow(stage);
        Thread.setDefaultUncaughtExceptionHandler(mainWindow::showError);

        Context context = new Context(mainWindow, args);

        RootController rootController = context.getRootController();

        loadEntities(rootController, mainWindow);

        initStage(stage,
                mainWindow.createMainWindow(rootController),
                mainWindow.getIconImage(),
                rootController);
    }

    private void initStage(Stage stage,
                           AnchorPane rootPane,
                           Image icon,
                           RootController rootController) {

        stage.setScene(new Scene(rootPane));
        stage.setTitle(Context.TITLE_APP);
        stage.getIcons().add(icon);

        stage.setOnShowing(event -> {
            log.info("showing callback");
            rootController.connect();
            rootController.startListenTransferQueue();
        });

        stage.setOnCloseRequest(handler -> {
            log.info("shutdown callback");

            rootController.close();
            log.info("Root controller is closed");

            Platform.exit();
            log.info("Platform.exit");
        });
        stage.setMaximized(true);
        stage.show();
    }

    private void loadEntities(RootController rootController,
                              MainWindow mainWindow) {

        Thread thread = new Thread(rootController::loadEntities);
        thread.setUncaughtExceptionHandler((t, e) -> {
            Platform.runLater(() -> {
                mainWindow.showError(t, e);
            });
        });
        thread.setDaemon(true);
        thread.start();
    }
}