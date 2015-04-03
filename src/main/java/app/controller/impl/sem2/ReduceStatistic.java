package app.controller.impl.sem2;

import app.backend.model.Pattern;
import app.backend.service.PatternService;
import app.controller.FxmlController;
import app.controller.impl.AbstractFxmlController;
import app.controller.impl.ProgressController;
import app.recognition.ErrorAnalyser;
import app.recognition.impl.CFourFive;
import app.reduce.Reduce;
import app.reduce.imp.AddReduce;
import app.reduce.imp.DelReduce;
import app.reduce.imp.ReducerBuilder;
import app.util.SpringFXMLLoader;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
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
public class ReduceStatistic extends AbstractFxmlController implements Initializable {

    private static final String DISTORTION_RATE = "Distortion Rate:";
    private int dataSize;

    @Autowired
    private PatternService patternService;
    @Autowired
    private SecondPartController secondPartController;

    @FXML
    private Slider distortionRate;
    @FXML
    private Label distLabel;
    @FXML
    private LineChart<Integer, Double> statisticChart;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        distortionRate.valueProperty().addListener((observable, oldValue, newValue) -> {
            distLabel.textProperty().setValue(DISTORTION_RATE + String.valueOf((int) distortionRate.getValue()));
        });
        distLabel.textProperty().setValue(DISTORTION_RATE + String.valueOf((int) distortionRate.getValue()));
        dataSize = patternService.getDataSize()/10;
    }

    public void buildStatistic() {
        statisticChart.getData().retainAll();
        Task<Void> task = new Task<Void>() {
            private List<XYChart.Series<Integer, Double>> seriesList = new ArrayList<>();

            @Override
            protected void succeeded() {
                super.succeeded();
                drawChart(seriesList);
            }

            @Override
            protected Void call() throws Exception {
               // List<Pattern> benchmarks = patternService.getBenchmarks();
                //List<Pattern> trainSet = ErrorAnalyser.newTrainSet(benchmarks, (int) distortionRate.getValue(), 0);
               // Map<Pattern, List<Pattern>> testSet = ErrorAnalyser.newTestSet(benchmarks, (int)distortionRate.getValue(), 0);

                List<Class<? extends Reduce>> classes = Arrays.asList(DelReduce.class, AddReduce.class);
                int maxOverview = dataSize*2;
                int currentOverview = 1;
                for (Class<? extends Reduce> r: classes) {
                    XYChart.Series<Integer, Double> series = new XYChart.Series<>();
                    series.setName(r.getSimpleName());
                    for(int i=1; i<dataSize; i++) {
                        ReducerBuilder reducerBuilder = new ReducerBuilder(r);
                   //     reducerBuilder.setBenchmarks(benchmarks).setDistortionRate((int)distortionRate.getValue()).
                     //           setSizeOfNewParamList(i).setTrainSet(trainSet);
                        Reduce reduce = reducerBuilder.build();
                        List<Integer> modifiedPatterns = reduce.reduce();

                      //  List<Pattern> newTrainSet = Reduce.reduceTrainSet(trainSet, modifiedPatterns);
                      //  Map<Pattern, List<Pattern>> newTestSet = Reduce.reduceTestSet(testSet, modifiedPatterns);
                       // List<Pattern> newBenchmarks = new ArrayList<>(newTestSet.keySet());
                       // series.getData().add(buildSeries(i, newBenchmarks, newTrainSet, newTestSet));
                        currentOverview++;
                        updateProgress(currentOverview, maxOverview-1);
                    }
                    seriesList.add(series);
                }

                return null;
            }
        };
        Thread t = new Thread(task);
        getProgressWindow().bind(task);
        t.start();
    }

    private void drawChart(List<XYChart.Series<Integer, Double>> seriesList) {
        for(XYChart.Series<Integer, Double> series: seriesList) {
            statisticChart.getData().add(series);
        }

    }

    private XYChart.Data<Integer, Double> buildSeries(int paramCount, List<Pattern> patterns, List<Pattern> trainSet, Map<Pattern, List<Pattern>> testSet) {
        //ErrorAnalyser errorAnalyser = new ErrorAnalyser(new CFourFive(patterns, trainSet), patterns);
        return null;//new XYChart.Data<>(paramCount, errorAnalyser.analise((int)distortionRate.getValue(), trainSet, testSet));
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
