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

        StartController startController = showStartWindow(stage);

        Thread thread = new Thread(() -> {
            RootController rootController = null;
            try {
                rootController = new Context(mainWindow, args).getRootController();
            } catch (Exception e) {
                Platform.runLater(() ->{
                    startController.setHeader("Ошибка инициализации");
                    startController.addLog(e.getMessage());
                });
            }

            if (rootController == null)
                return;

            RootController finalRootController = rootController;
            Platform.runLater(() -> {
                initStage(stage,
                        mainWindow.createMainWindow(finalRootController),
                        mainWindow.getIconImage(),
                        finalRootController);
            });
        });
        thread.setDaemon(true);
        thread.start();
    }

    private static StartController showStartWindow(Stage stage) {
        StartController startController = new StartController();
        stage.setScene(new Scene(startController.getStartPane("Инициализация")));
        stage.show();
        return startController;
    }

    private void initStage(Stage stage,
                           AnchorPane rootPane,
                           Image icon,
                           RootController rootController) {
        stage.hide();
        stage.setScene(new Scene(rootPane));
        stage.setTitle(Context.TITLE_APP);
        stage.getIcons().add(icon);

        stage.setOnShowing(event -> {
            log.info("showing callback");
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
}