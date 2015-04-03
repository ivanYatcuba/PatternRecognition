package app.controller.impl;

import app.backend.model.Pattern;
import app.util.ImageWrapper;
import app.util.pattern.ImagePatternLoader;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ProgressController extends AbstractFxmlController {


    @FXML
    private ProgressBar progress;
    @FXML
    private Label progressLabel;
    @FXML
    private ImageView view;
    @FXML
    private ImageView view1;
    @FXML
    private ImageView view11;
    @FXML
    private ImageView view111;
    @FXML
    private ImageView view1111;
    @FXML
    private ImageView view11111;
    @FXML
    private Button close;

    @Autowired
    private ImagePatternLoader imagePatternLoader;


    public void bind(final Task task){
        close.setDisable(true);
        progress.setProgress(0);
        progress.progressProperty().bind(task.progressProperty());
        progressLabel.textProperty().bind(task.messageProperty());
        progress.progressProperty().addListener((observableValue, number, number2) -> {
            if (task.getWorkDone() >= task.getTotalWork()) {
                progress.progressProperty().unbind();
                close.setDisable(false);
            }
        });
    }

    public void close() {
        ((Stage) getView().getScene().getWindow()).close();
    }

    public void changeImage(List<Pattern> patterns, List<Integer> indexes, Integer newIndex) {
        Pattern p = new Pattern();
        p.setImage(patterns.get(0).getImage());
        setReds(p, indexes);
        if(newIndex != null) {
            p.setPixel(newIndex, ImageWrapper.BLUE);
        }
        imagePatternLoader.loadPattern(p, view);
        try {
            p.setImage(patterns.get(1).getImage());
            setReds(p, indexes);
            imagePatternLoader.loadPattern(p, view1);
            p.setImage(patterns.get(2).getImage());
            setReds(p, indexes);
            imagePatternLoader.loadPattern(p, view11);
            p.setImage(patterns.get(3).getImage());
            setReds(p, indexes);
            imagePatternLoader.loadPattern(p, view111);
            p.setImage(patterns.get(4).getImage());
            setReds(p, indexes);
            imagePatternLoader.loadPattern(p, view1111);
            p.setImage(patterns.get(5).getImage());
            setReds(p, indexes);
            imagePatternLoader.loadPattern(p, view11111);
        } catch (IndexOutOfBoundsException | NullPointerException e) {

        }
    }

    private void setReds(Pattern p, List<Integer> indexes) {
        for(Integer i: indexes) {
            p.setPixel(i, ImageWrapper.RED);
        }
    }
}
