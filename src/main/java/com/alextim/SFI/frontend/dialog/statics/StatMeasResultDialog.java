package com.alextim.SFI.frontend.dialog.statics;

import com.alextim.SFI.frontend.view.statics.StaticController.StatMeasResult;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import lombok.SneakyThrows;

public class StatMeasResultDialog extends Dialog<StatMeasResult> {
    private final String FXML_FILE = "staticResult.fxml";
    private final String TITLE = "Обновление статических параметров";

    public StatMeasResultDialog(StatMeasResult staticParam) {
        init(staticParam);

        setTitle(TITLE);
    }

    @SneakyThrows
    private void init(StatMeasResult statMeasResult) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_FILE));
        getDialogPane().setContent(loader.load());
        getDialogPane().getButtonTypes().add(ButtonType.APPLY);
        StatMeasResultController controller = loader.getController();
        if(statMeasResult != null) {
            controller.setStatMeasResult(statMeasResult);
            ((Button)getDialogPane().lookupButton(ButtonType.APPLY)).setText("OK");
        }

        setResultConverter(buttonType -> {
            if(buttonType != null && buttonType.equals(ButtonType.APPLY))
                return controller.getResult();
            return null;
        });

        setResizable(true);
    }
}
