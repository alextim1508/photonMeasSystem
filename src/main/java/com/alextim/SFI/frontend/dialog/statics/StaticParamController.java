package com.alextim.SFI.frontend.dialog.statics;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import static com.alextim.SFI.frontend.view.statics.StaticController.StaticParam;

public class StaticParamController {

    @FXML
    private TextField prm1ch1Max, prm1ch1MaxFon, prm1ch1MinFon;
    @FXML
    private TextField prm1ch2Max, prm1ch2MaxFon, prm1ch2MinFon;

    @FXML
    private TextField prm2ch1Max, prm2ch1MaxFon, prm2ch1MinFon;

    @FXML
    private TextField prm2ch2Max, prm2ch2MaxFon, prm2ch2MinFon;

    public void setStaticParam(StaticParam staticParam) {
        prm1ch1Max.setText(Long.toString(staticParam.prm1ch1Max));
        prm1ch1MaxFon.setText(Long.toString(staticParam.prm1ch1MaxFon));
        prm1ch1MinFon.setText(Long.toString(staticParam.prm1ch1MinFon));

        prm1ch2Max.setText(Long.toString(staticParam.prm1ch2Max));
        prm1ch2MaxFon.setText(Long.toString(staticParam.prm1ch2MaxFon));
        prm1ch2MinFon.setText(Long.toString(staticParam.prm1ch2MinFon));

        prm2ch1Max.setText(Long.toString(staticParam.prm2ch1Max));
        prm2ch1MaxFon.setText(Long.toString(staticParam.prm2ch1MaxFon));
        prm2ch1MinFon.setText(Long.toString(staticParam.prm2ch1MinFon));

        prm2ch2Max.setText(Long.toString(staticParam.prm2ch2Max));
        prm2ch2MaxFon.setText(Long.toString(staticParam.prm2ch2MaxFon));
        prm2ch2MinFon.setText(Long.toString(staticParam.prm2ch2MinFon));
    }

    public StaticParam getResult() {
        long prm1ch1Max = Long.parseLong(this.prm1ch1Max.getText());
        long prm1ch1MaxFon = Long.parseLong(this.prm1ch1MaxFon.getText());
        long prm1ch1MinFon = Long.parseLong(this.prm1ch1MinFon.getText());

        long prm1ch2Max = Long.parseLong(this.prm1ch2Max.getText());
        long prm1ch2MaxFon = Long.parseLong(this.prm1ch2MaxFon.getText());
        long prm1ch2MinFon = Long.parseLong(this.prm1ch2MinFon.getText());

        long prm2ch1Max = Long.parseLong(this.prm2ch1Max.getText());
        long prm2ch1MaxFon = Long.parseLong(this.prm2ch1MaxFon.getText());
        long prm2ch1MinFon = Long.parseLong(this.prm2ch1MinFon.getText());

        long prm2ch2Max = Long.parseLong(this.prm2ch2Max.getText());
        long prm2ch2MaxFon = Long.parseLong(this.prm2ch2MaxFon.getText());
        long prm2ch2MinFon = Long.parseLong(this.prm2ch2MinFon.getText());

        return StaticParam.builder()
                .prm1ch1Max(prm1ch1Max)
                .prm1ch1MaxFon(prm1ch1MaxFon)
                .prm1ch1MinFon(prm1ch1MinFon)
                .prm1ch2Max(prm1ch2Max)
                .prm1ch2MaxFon(prm1ch2MaxFon)
                .prm1ch2MinFon(prm1ch2MinFon)
                .prm2ch1Max(prm2ch1Max)
                .prm2ch1MaxFon(prm2ch1MaxFon)
                .prm2ch1MinFon(prm2ch1MinFon)
                .prm2ch2Max(prm2ch2Max)
                .prm2ch2MaxFon(prm2ch2MaxFon)
                .prm2ch2MinFon(prm2ch2MinFon)
                .build();
    }

}
