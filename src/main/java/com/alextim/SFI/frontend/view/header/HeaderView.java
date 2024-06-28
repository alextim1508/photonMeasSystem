package com.alextim.SFI.frontend.view.header;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Arrays;
import java.util.ResourceBundle;

public class HeaderView {
    public Pane getView(ResourceBundle bundle) {
        try {
            return FXMLLoader.<AnchorPane>load(HeaderView.class.getResource("HeaderView.fxml"), bundle);
        } catch (IOException e) {
            return new Pane(new TextArea(
                    "Error" + e.getMessage() + "/ " + Arrays.toString(e.getStackTrace())));
        }
    }
}
