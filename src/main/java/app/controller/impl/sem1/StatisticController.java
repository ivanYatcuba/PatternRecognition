package app.controller.impl.sem1;

import app.backend.model.Pattern;
import app.backend.service.PatternService;
import app.controller.impl.AbstractFxmlController;
import app.recognition.ErrorAnalyser;
import app.recognition.Recognizer;
import app.recognition.impl.CFourFive;
import app.recognition.impl.KNN;
import app.recognition.impl.SolutionTreeBagging;
import app.util.RecognizerFactory;
import app.util.TestSetFactory;
import app.util.TrainSetFactory;
import app.util.pattern.Distorter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;

@Controller
public class StatisticController extends AbstractFxmlController implements Initializable {

    public static final int KNN_K = 3;
    private static final int PROTOTYPE_PER_PATTERN_SIZE = 10;

    @Autowired
    private PatternService patternService;
    @Autowired
    private RecognizerFactory recognizerFactory;

    @FXML
    private LineChart<Integer, Double> activityChart;

    private List<Pattern> patterns;

    public void buildStatistic() {
        activityChart.getData().retainAll();

        TrainSetFactory trainSetFactory = new TrainSetFactory(PROTOTYPE_PER_PATTERN_SIZE);

        Random rand = new Random();
        int distortion = rand.nextInt((30) + 1);
        List<Pattern> trainSet = trainSetFactory.generateTrainSet(patterns, distortion);

        if(!patterns.isEmpty()) {
            KNN knn = recognizerFactory.getKNN(KNN_K, trainSet, patterns);
            CFourFive cFourFive = recognizerFactory.getCFourFive(patterns, trainSet);
            SolutionTreeBagging solutionTreeBagging = recognizerFactory.getSolutionTreeBagging(patterns, trainSet);

            activityChart.getData().add(buildSeries("KNN", knn));
            activityChart.getData().add(buildSeries("C4.5", cFourFive));
            activityChart.getData().add(buildSeries("Bagging", solutionTreeBagging));
        }
    }

    private XYChart.Series<Integer, Double> buildSeries( String seriesName, Recognizer recognizer) {
        XYChart.Series<Integer, Double> series = new XYChart.Series<>();
        series.setName(seriesName);
        int distortionRate = 0;
        while (distortionRate <= 100) {
            TestSetFactory testSetFactory = new TestSetFactory(PROTOTYPE_PER_PATTERN_SIZE);
            Map<Pattern, List<Pattern>> testSet = testSetFactory.newTestSet(patterns, distortionRate);
            ErrorAnalyser errorAnalyser = new ErrorAnalyser(patterns);
            series.getData().add(new XYChart.Data<>(distortionRate, errorAnalyser.analise(recognizer, testSet)));
            distortionRate += 5;
        }
        return series;
    }

    @Override
    public void initialize(final URL url, final ResourceBundle resourceBundle) {
        patterns = patternService.getBenchmarks();
        buildStatistic();
    }
}
