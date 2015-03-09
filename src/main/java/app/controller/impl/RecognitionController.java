package app.controller.impl;

import app.backend.model.Pattern;
import app.backend.service.PatternService;
import app.controller.FxmlController;
import app.recognition.Recognizer;
import app.recognition.impl.CFourFive;
import app.recognition.impl.KNN;
import app.recognition.impl.SolutionTreeBagging;
import app.util.pattern.Distorter;
import app.util.pattern.ImagePatternLoader;
import app.util.RecognizerFactory;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.*;

@Controller
public class RecognitionController extends AbstractFxmlController implements  Initializable {

    private static final int KNN_K = 3;
    private static final String IMG_EMPTY_JPG = "img/empty.png";
    private Distorter distorter = new Distorter();

    @Autowired
    private PatternService patternService;
    @Autowired
    private ImagePatternLoader imagePatternLoader;
    @Autowired
    private RecognizerFactory recognizerFactory;

    @FXML
    private ImageView data_view;
    @FXML
    private ImageView recognized_view;
    @FXML
    private Slider distRate;
    @FXML
    private ComboBox<Pattern> patterns;
    @FXML
    private ComboBox<Recognizer> methods;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        data_view.setImage(new Image(IMG_EMPTY_JPG));
        recognized_view.setImage(new Image(IMG_EMPTY_JPG));
        List<Pattern> benchmarks = patternService.getBenchmarks();
        if (!benchmarks.isEmpty()) {
            patterns.setItems(FXCollections.observableArrayList(benchmarks));
            patterns.getSelectionModel().select(0);
            List<Pattern> trainSet = patternService.getAll();
            int attributeLength = patterns.getSelectionModel().getSelectedItem().getData().length;
            KNN knn = recognizerFactory.getKNN(KNN_K, trainSet, benchmarks);
            CFourFive cFourFive = recognizerFactory.getCFourFive(benchmarks, trainSet, attributeLength);
            SolutionTreeBagging solutionTreeBagging = recognizerFactory.getSolutionTreeBagging(benchmarks, trainSet, attributeLength);
            methods.setItems(FXCollections.observableArrayList(Arrays.asList(knn, cFourFive, solutionTreeBagging)));
            methods.getSelectionModel().select(0);
            generateAndRecognize();
        }
    }

    public void generateAndRecognize() {
        Pattern pattern = patterns.getSelectionModel().getSelectedItem();
        if(pattern != null) {
            generateAndRecognizeAction(patterns.getSelectionModel().getSelectedItem());
        }
    }

    private void generateAndRecognizeAction(Pattern selectedBenchmark) {
        Pattern distorted = distorter.distort(selectedBenchmark, 1, (int) distRate.getValue()).get(0);
        Recognizer recognizer = methods.getValue();
        imagePatternLoader.loadPattern(distorted, data_view);
        imagePatternLoader.loadPattern(recognizer.recognize(distorted), recognized_view);
    }
}
