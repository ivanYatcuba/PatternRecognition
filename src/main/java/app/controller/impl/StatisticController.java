package app.controller.impl;

import app.backend.model.Pattern;
import app.backend.service.PatternService;
import app.recognition.ErrorAnalyser;
import app.recognition.Recognizer;
import app.recognition.impl.CFourFive;
import app.recognition.impl.KNN;
import app.recognition.impl.SolutionTreeBagging;
import app.util.RecognizerFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class StatisticController extends AbstractFxmlController implements Initializable {

    public static final int KNN_K = 3;

    @Autowired
    private PatternService patternService;
    @Autowired
    private RecognizerFactory recognizerFactory;

    @FXML
    private LineChart<Integer, Double> activityChart;

    private List<Pattern> patterns;
    private List<Pattern> trainSet;

    public void buildStatistic() {
        activityChart.getData().retainAll();
        patterns = patternService.getBenchmarks();
        trainSet = patternService.getAll();
        if(!patterns.isEmpty()) {
            int attributesLength = patterns.get(0).getData().length;
            KNN knn = recognizerFactory.getKNN(KNN_K, trainSet, patterns);
            CFourFive cFourFive = recognizerFactory.getCFourFive(patterns, trainSet, attributesLength);
            SolutionTreeBagging solutionTreeBagging = recognizerFactory.getSolutionTreeBagging(patterns, trainSet, attributesLength);
            activityChart.getData().add(buildSeries(knn, "KNN"));
            activityChart.getData().add(buildSeries(cFourFive, "C4.5"));
            activityChart.getData().add(buildSeries(solutionTreeBagging, "Bagging"));
        }
    }

    private XYChart.Series<Integer, Double> buildSeries(Recognizer recognizer, String seriesName) {
        XYChart.Series<Integer, Double> series = new XYChart.Series<>();
        series.setName(seriesName);
        int distortionRate = 0;
        ErrorAnalyser errorAnalyser = new ErrorAnalyser(recognizer, patterns);
        while (distortionRate <= 100) {
            int errorsNum = errorAnalyser.analise(distortionRate);
            series.getData().add(new XYChart.Data<>
                    (distortionRate, ((double)errorsNum/(double)trainSet.size()) *100));
            distortionRate += 5;
        }
        return series;
    }

    @Override
    public void initialize(final URL url, final ResourceBundle resourceBundle) {
        buildStatistic();
    }
}
