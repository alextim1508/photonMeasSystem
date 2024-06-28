package com.alextim.SFI.frontend.view.takingStat;

import com.alextim.SFI.frontend.view.NodeController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TakingStatController extends NodeController {

    @Override
    protected String getName() {
        return getClass().getSimpleName();
    }

    @FXML
    void onOpen(ActionEvent event) {
        log.info(" open ");

        rootController.getPhotonMeasSystemService().openTransmitter();
    }

    @FXML
    void onClose(ActionEvent event) {
        log.info(" close ");

        rootController.getPhotonMeasSystemService().closeTransmitter();
    }

}
