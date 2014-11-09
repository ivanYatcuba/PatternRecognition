package app.controller.impl;

import app.backend.model.Pattern;
import app.backend.service.PatternService;
import app.controller.IController;
import app.recognition.Recognizer;
import app.recognition.impl.CFourFive;
import app.recognition.impl.KNN;
import app.recognition.impl.SolutionTreeBagging;
import app.util.Distorter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class StatisticController implements IController, Initializable {

    public static final int KNN_K = 3;
    private Node view;

    @Autowired
    private PatternService patternService;
    @Autowired
    private KNN knn;
    @Autowired
    private CFourFive cFourFive;
    @Autowired
    private Distorter distorter;
    @Autowired
    private SolutionTreeBagging solutionTreeBagging;

    @FXML
    private LineChart<Integer, Double> activityChart;

    private List<Pattern> patterns;
    private List<Pattern> trainSet;
    @PostConstruct
    public void init() {
        patterns = patternService.getBenchmarks();
        trainSet = patternService.getAll();
    }




    @Override
    public Node getView() {
        return view;
    }

    @Override
    public void setView(Node view) {
        this.view = view;
    }

    public void buildStatistic() {
        activityChart.getData().retainAll();
        prepareKNN();
        prepareCFourFive();
        prepareBagging();
        activityChart.getData().add(buildSeries(knn, "KNN"));
        activityChart.getData().add(buildSeries(cFourFive, "C4.5"));
        activityChart.getData().add(buildSeries(solutionTreeBagging, "Bagging"));
    }

    private XYChart.Series<Integer, Double> buildSeries(Recognizer recognizer, String seriesName) {
        XYChart.Series<Integer, Double> series = new XYChart.Series<>();
        series.setName(seriesName);
        int errorsNum = 0;
        int distortionRate = 0;
        List<Pattern> benchmarks = patternService.getBenchmarks();
        while (distortionRate <= 100) {

            List<Pattern> newTrainSet = new ArrayList<>();
            for(Pattern p : benchmarks){
                List<Pattern> distortedData = distorter.distort(p, 10, distortionRate);
                newTrainSet.addAll(distortedData);
            }
            recognizer.setTrainSet(newTrainSet);
            recognizer.init();

            for (Pattern p : benchmarks) {
                List<Pattern> distortedData = distorter.distort(p, 10, distortionRate);
                for (Pattern distortedPattern : distortedData) {
                    Pattern parent = recognizer.recognize(distortedPattern);
                    if (p.getId() != parent.getId()) {errorsNum++;}
                }
            }
            series.getData().add(new XYChart.Data<>(distortionRate, ((double)errorsNum/(double)trainSet.size())
              *100));
            distortionRate += 5;
            errorsNum = 0;
        }
        return series;
    }

    private void prepareKNN() {
        knn.setK(KNN_K);
        knn.setTrainSet(trainSet);
    }

    private void prepareCFourFive(){
        cFourFive.setBenchmarks(patterns);
        cFourFive.setTrainSet(trainSet);
        cFourFive.setAttributesCount(patterns.get(0).getData().length);
    }

    private void prepareBagging(){
        solutionTreeBagging.setBenchmarks(patterns);
        solutionTreeBagging.setTrainSet(trainSet);
        solutionTreeBagging.setAttributesCount(patterns.get(0).getData().length);
    }

    @Override
    public void initialize(final URL url, final ResourceBundle resourceBundle) {
        //buildStatistic();
    }
}
