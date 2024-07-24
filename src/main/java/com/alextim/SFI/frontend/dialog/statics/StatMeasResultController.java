package com.alextim.SFI.frontend.dialog.statics;

import com.alextim.SFI.frontend.view.statics.StaticController.StatMeasResult;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class StatMeasResultController {

    @FXML
    private TextField refHeight;

    @FXML
    private TextField frequency4;
    @FXML
    private TextField frequency1;
    @FXML
    private TextField frequency3;
    @FXML
    private TextField frequency2;

    private StatMeasResult statMeasResult;

    public void setStatMeasResult(StatMeasResult statMeasResult) {
        this.statMeasResult = statMeasResult;
        refHeight.setText(Long.toString(statMeasResult.refHeight));
        frequency1.setText(Double.toString(statMeasResult.frequency1));
        frequency2.setText(Double.toString(statMeasResult.frequency2));
        frequency3.setText(Double.toString(statMeasResult.frequency3));
        frequency4.setText(Double.toString(statMeasResult.frequency4));
    }

    public StatMeasResult getResult() {
        if(statMeasResult == null) {
            long refHeight = Long.parseLong(this.refHeight.getText());
            double frequency1 = Double.parseDouble(this.frequency1.getText());
            double frequency2 = Double.parseDouble(this.frequency2.getText());
            double frequency3 = Double.parseDouble(this.frequency3.getText());
            double frequency4 = Double.parseDouble(this.frequency4.getText());
            return new StatMeasResult(refHeight, frequency1, frequency2, frequency3, frequency4);
        } else {
            statMeasResult.refHeight = Long.parseLong(this.refHeight.getText());
            statMeasResult.frequency1 = Double.parseDouble(this.frequency1.getText());
            statMeasResult.frequency2 = Double.parseDouble(this.frequency2.getText());
            statMeasResult.frequency3 = Double.parseDouble(this.frequency3.getText());
            statMeasResult.frequency4 = Double.parseDouble(this.frequency4.getText());
            return statMeasResult;
        }
    }
}
