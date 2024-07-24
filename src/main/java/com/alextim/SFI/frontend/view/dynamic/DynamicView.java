package com.alextim.SFI.frontend.view.dynamic;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Arrays;
import java.util.ResourceBundle;

public class DynamicView {
    public Pane getView(ResourceBundle bundle) {
        try {
            return FXMLLoader.<AnchorPane>load(DynamicView.class.getResource("DynamicView.fxml"), bundle);
        } catch (IOException e) {
            return new Pane(new TextArea(
                    "Error" + e.getMessage() + "/ " + Arrays.toString(e.getStackTrace())));
        }
    }
}
