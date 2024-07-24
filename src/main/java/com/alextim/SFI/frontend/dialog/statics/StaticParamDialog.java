package com.alextim.SFI.frontend.dialog.statics;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import lombok.SneakyThrows;

import static com.alextim.SFI.frontend.view.statics.StaticController.StaticParam;

public class StaticParamDialog extends Dialog<StaticParam> {

    private final String FXML_FILE = "staticParam.fxml";
    private final String TITLE = "Параметры для экспорта";

    public StaticParamDialog(StaticParam staticParam) {
        init(staticParam);

        setTitle(TITLE);
    }

    @SneakyThrows
    private void init(StaticParam staticParam) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_FILE));
        getDialogPane().setContent(loader.load());
        getDialogPane().getButtonTypes().add(ButtonType.APPLY);
        StaticParamController controller = loader.getController();
        if(staticParam != null) {
            controller.setStaticParam(staticParam);
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