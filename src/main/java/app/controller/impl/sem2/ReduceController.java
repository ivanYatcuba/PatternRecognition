package app.controller.impl.sem2;

import app.backend.model.Pattern;
import app.backend.service.PatternService;
import app.controller.FxmlController;
import app.controller.impl.AbstractFxmlController;
import app.controller.impl.ProgressController;
import app.recognition.ErrorAnalyser;
import app.recognition.impl.CFourFive;
import app.recognition.impl.KNN;
import app.recognition.impl.SolutionTreeBagging;
import app.reduce.Reduce;
import app.reduce.imp.AddReduce;
import app.reduce.imp.DelReduce;
import app.reduce.imp.ReducerBuilder;
import app.util.SpringFXMLLoader;
import app.util.TestSetFactory;
import app.util.TrainSetFactory;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
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
    private static final int PROTOTYPE_PER_PATTERN_SIZE = 10;

    @Autowired
    private SecondPartController secondPartController;
    @Autowired
    private PatternService patternService;
    private int dataSize;

    List<Pattern> benchmarks;

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
    @FXML
    private CheckBox enableVisualisation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Class<? extends Reduce>> reduces = new ArrayList<>();
        reduces.add(DelReduce.class);
        reduces.add(AddReduce.class);
        reducers.setConverter(new StringConverter<Class<? extends Reduce>>() {
            @Override
            public String toString(Class<? extends Reduce> object) {
                return object.getSimpleName();
            }

            @Override
            public Class<? extends Reduce> fromString(String string) {
                if(string.equals(DelReduce.class.getSimpleName())) {
                    return Reduce.class;
                }
                if(string.equals(AddReduce.class.getSimpleName())) {
                    return AddReduce.class;
                }
                throw new IllegalArgumentException();
            }
        });
        benchmarks = patternService.getBenchmarks();
        reducers.setItems(FXCollections.observableArrayList(reduces));
        reducers.getSelectionModel().select(0);
        if(!benchmarks.isEmpty()) {
            dataSize = benchmarks.get(0).getImage().getDataSize();
        } else {
            dataSize = 1;
        }
        propCount.setMax(dataSize);
        propCount.setMin(1);
        propCount.setValue(dataSize);
        propCount.valueProperty().addListener((observable, oldValue, newValue) -> {
            propLabel.textProperty().setValue(NEW_PROPERTY_COUNT + String.valueOf((int) propCount.getValue()));
        });
        distortionRate.valueProperty().addListener((observable, oldValue, newValue) -> {
            distLabel.textProperty().setValue(DISTORTION_RATE + String.valueOf((int) distortionRate.getValue()));
        });
        propLabel.textProperty().setValue(NEW_PROPERTY_COUNT + String.valueOf((int) propCount.getValue()));
        distLabel.textProperty().setValue(DISTORTION_RATE + String.valueOf((int) distortionRate.getValue()));


        results.getYAxis().setAutoRanging(false);
        enableVisualisation.setSelected(true);
        ((NumberAxis)results.getYAxis()).setUpperBound(100);
    }

    public void startReduce() {
        results.getData().retainAll();
        ProgressController progressController = getProgressWindow();
        ReducerBuilder reducerBuilder = new ReducerBuilder(reducers.getSelectionModel().getSelectedItem());
        reducerBuilder.setBenchmarks(patternService.getBenchmarks()).setDistortionRate((int)distortionRate.getValue()).
                setSizeOfNewParamList(dataSize - (int)propCount.getValue()).setProgressController(progressController).
                setVisulaization(enableVisualisation.isSelected());
        Reduce reduce = reducerBuilder.build();
        Task task = (Task)reduce;
        task.setOnSucceeded(event -> buildReduceResults(((Reduce)event.getSource()).getReduceResults()));
        Thread t = new Thread(task);
        progressController.bind(task);
        t.start();
    }

    public void buildReduceResults(List<Integer>  indexToIgnore) {
        TrainSetFactory trainSetFactory = new TrainSetFactory(PROTOTYPE_PER_PATTERN_SIZE);
        List<Pattern> trainSet = trainSetFactory.generateTrainSet(benchmarks, (int) distortionRate.getValue());
        TestSetFactory testSetFactory = new TestSetFactory(PROTOTYPE_PER_PATTERN_SIZE);
        Map<Pattern, List<Pattern>> testSet = testSetFactory.newTestSet(benchmarks, (int) distortionRate.getValue());

        results.getData().add(buildSeries("Origin", benchmarks, trainSet, testSet, new ArrayList<>()));
        results.getData().add(buildSeries("Modified", benchmarks, trainSet, testSet, indexToIgnore));

    }

    private XYChart.Series<String, Number> buildSeries(String name, List<Pattern> patterns, List<Pattern> trainSet,
                                                       Map<Pattern, List<Pattern>> testSet, List<Integer> indexToIgnore) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(name);

        KNN knn = new KNN(3, trainSet, patterns);
        knn.setAttributesToIgnore(indexToIgnore);
        ErrorAnalyser errorAnalyser = new ErrorAnalyser(patterns);
        series.getData().add(new XYChart.Data<>(KNN,  errorAnalyser.analise(knn, testSet)));


        CFourFive cFourFive = new CFourFive(patterns, trainSet);
        cFourFive.setAttributesToIgnore(indexToIgnore);
        series.getData().add(new XYChart.Data<>(C4_5, errorAnalyser.analise(cFourFive, testSet)));

        SolutionTreeBagging solutionTreeBagging = new SolutionTreeBagging(patterns, trainSet);
        solutionTreeBagging.setAttributesToIgnore(indexToIgnore);
        series.getData().add(new XYChart.Data<>(SOLUTION_TREE_BAGGING, errorAnalyser.analise(solutionTreeBagging, testSet)));
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
