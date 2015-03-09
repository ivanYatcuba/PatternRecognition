package app.controller.impl;

import app.backend.model.Pattern;
import app.backend.service.PatternService;
import app.controller.FxmlController;
import app.reduce.Reduce;
import app.reduce.imp.AddReduce;
import app.reduce.imp.DelReduce;
import app.reduce.imp.ReducerBuilder;
import app.util.SpringFXMLLoader;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class ReduceController extends AbstractFxmlController implements Initializable {

    private static String NEW_PROPERTY_COUNT = "New Property Count:";
    private static String DISTORTION_RATE = "Distortion Rate:";

    @Autowired
    private SecondPartController secondPartController;
    @Autowired
    private PatternService patternService;

    @FXML
    private ComboBox<Class<? extends Reduce>> reducers;
    @FXML
    private Slider propCount;
    @FXML
    private Slider distortionRate;
    @FXML
    private Label propLabel;
    @FXML
    private Label distLabel;
    @FXML
    private BarChart<String, Number> results;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Class<? extends Reduce>> reduces = new ArrayList<>();
        reduces.add(DelReduce.class);
        reduces.add(AddReduce.class);
        reducers.setItems(FXCollections.observableArrayList(reduces));
        reducers.getSelectionModel().select(0);
        propCount.setMax(patternService.getDataSize());
        propCount.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                propLabel.textProperty().setValue(NEW_PROPERTY_COUNT + String.valueOf((int) propCount.getValue()));
            }
        });
        distortionRate.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                distLabel.textProperty().setValue(DISTORTION_RATE + String.valueOf((int) distortionRate.getValue()));
            }
        });
    }

    public void startReduce() {
        ReducerBuilder reducerBuilder = new ReducerBuilder(reducers.getSelectionModel().getSelectedItem());
        reducerBuilder.setBenchmarks(patternService.getBenchmarks()).setDistortionRate((int)distortionRate.getValue()).
                setSizeOfNewParamList((int)propCount.getValue());
        Reduce reduce = reducerBuilder.build();
        Task task = (Task)reduce;
        Thread t = new Thread(task);
        getProgressWindow().bind(task);
        t.start();
    }

    public void buildReduceResults(List<Pattern> modifiedPatterns) {
        XYChart.Series origin = new XYChart.Series();
        origin.setName("Origin");
        origin.getData().add(new XYChart.Data(austria, 25601.34));
    }

    private ProgressController getProgressWindow() {
        Stage stage = new Stage();
        FxmlController progressController = SpringFXMLLoader.load("/fxml/progress.fxml");
        Parent root = (Parent) progressController.getView();
        stage.setScene(new Scene(root));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner((secondPartController.getView().getScene().getWindow()));
        stage.show();
        return (ProgressController)progressController;
    }

}
