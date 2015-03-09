package app.controller.impl;

import app.backend.model.Pattern;
import app.backend.service.PatternService;
import app.controller.FxmlController;
import app.recognition.ErrorAnalyser;
import app.recognition.impl.CFourFive;
import app.recognition.impl.KNN;
import app.recognition.impl.SolutionTreeBagging;
import app.reduce.Reduce;
import app.reduce.imp.AddReduce;
import app.reduce.imp.DelReduce;
import app.reduce.imp.ReducerBuilder;
import app.util.SpringFXMLLoader;
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
import java.util.*;

@Controller
public class ReduceController extends AbstractFxmlController implements Initializable {

    private static final String NEW_PROPERTY_COUNT = "New Property Count:";
    private static final String DISTORTION_RATE = "Distortion Rate:";
    private static final String KNN = "KNN";
    private static final String C4_5 = "C4.5";
    private static final String SOLUTION_TREE_BAGGING = "Solution Tree Bagging";

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
        propCount.valueProperty().addListener((observable, oldValue, newValue) -> {
            propLabel.textProperty().setValue(NEW_PROPERTY_COUNT + String.valueOf((int) propCount.getValue()));
        });
        distortionRate.valueProperty().addListener((observable, oldValue, newValue) -> {
            distLabel.textProperty().setValue(DISTORTION_RATE + String.valueOf((int) distortionRate.getValue()));
        });
    }

    public void startReduce() {
        ReducerBuilder reducerBuilder = new ReducerBuilder(reducers.getSelectionModel().getSelectedItem());
        reducerBuilder.setBenchmarks(patternService.getBenchmarks()).setDistortionRate((int)distortionRate.getValue()).
                setSizeOfNewParamList((int)propCount.getValue());
        Reduce reduce = reducerBuilder.build();
        Task task = (Task)reduce;
        task.setOnSucceeded(event -> buildReduceResults(((Reduce)event.getSource()).getReduceResults()));
        Thread t = new Thread(task);
        getProgressWindow().bind(task);
        t.start();
    }

    public void buildReduceResults(List<Integer> modifiedPatterns) {
        results.getData().retainAll();
        List<Pattern> benchmarks = patternService.getBenchmarks();
        List<Pattern> trainSet = ErrorAnalyser.newTrainSet(benchmarks, (int)distortionRate.getValue());
        Map<Pattern, List<Pattern>> testSet = ErrorAnalyser.newTestSet(benchmarks, (int)distortionRate.getValue());
        results.getData().add(buildSeries("Origin", benchmarks, trainSet, testSet));

        trainSet = Reduce.reduceTrainSet(trainSet, modifiedPatterns);
        testSet = Reduce.reduceTestSet(testSet, modifiedPatterns);
        List<Pattern> newBenchmark = new ArrayList<>(testSet.keySet());
        results.getData().add(buildSeries("Modified", newBenchmark, trainSet, testSet));

    }

    private XYChart.Series<String, Number> buildSeries(String name, List<Pattern> patterns, List<Pattern> trainSet,
                                                       Map<Pattern, List<Pattern>> testSet) {
        int distortionRate = (int) this.distortionRate.getValue();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        int dataSize = patterns.get(0).getData().length;
        int errorSize;
        series.setName(name);

        ErrorAnalyser errorAnalyser = new ErrorAnalyser(new KNN(3, null, patterns), patterns);
        errorSize = errorAnalyser.analise(distortionRate, trainSet, testSet);
        series.getData().add(new XYChart.Data<>(KNN, errorSize));


        errorAnalyser = new ErrorAnalyser(new CFourFive(patterns, null, dataSize), patterns);
        errorSize = errorAnalyser.analise(distortionRate, trainSet, testSet);
        series.getData().add(new XYChart.Data<>(C4_5, errorSize));

        errorAnalyser = new ErrorAnalyser(new SolutionTreeBagging(patterns, null, dataSize), patterns);
        errorSize = errorAnalyser.analise(distortionRate, trainSet, testSet);
        series.getData().add(new XYChart.Data<>(SOLUTION_TREE_BAGGING, errorSize));
        return  series;
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
