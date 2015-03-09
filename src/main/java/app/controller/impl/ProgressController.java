package app.controller.impl;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import org.springframework.stereotype.Controller;

@Controller
public class ProgressController extends AbstractFxmlController {

    @FXML
    private ProgressBar progress;
    @FXML
    private Label progressLabel;

    public void bind(final Task task){
        progress.setProgress(0);
        progress.progressProperty().bind(task.progressProperty());
        progressLabel.textProperty().bind(task.messageProperty());
        progress.progressProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                if (task.getWorkDone() >= task.getTotalWork()) {
                    progress.progressProperty().unbind();
                    task.cancel();
                    ((Stage) getView().getScene().getWindow()).close();
                }
            }
        });
    }
}