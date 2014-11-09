package app.controller.impl;

import app.backend.model.Pattern;
import app.backend.service.PatternService;
import app.controller.IController;
import app.recognition.Recognizer;
import app.recognition.impl.CFourFive;
import app.recognition.impl.KNN;
import app.recognition.impl.SolutionTreeBagging;
import app.util.Distorter;
import app.util.ImagePatternLoader;
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
public class RecognitionController implements IController, Initializable {

    private static final int KNN_K = 3;
    private Node view;

    @Autowired
    private PatternService patternService;
    @Autowired
    private ImagePatternLoader imagePatternLoader;
    @Autowired
    private KNN knn;
    @Autowired
    private CFourFive cFourFive;
    @Autowired
    private Distorter distorter;
    @Autowired
    private SolutionTreeBagging solutionTreeBagging;


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
    public Node getView() {
        return view;
    }

    @Override
    public void setView(Node view) {
        this.view = view;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        data_view.setImage(new Image("img/empty.jpg"));
        recognized_view.setImage(new Image("img/empty.jpg"));
        patterns.setItems(FXCollections.observableArrayList(patternService.getBenchmarks()));
        patterns.getSelectionModel().select(0);
        prepareKNN();
        prepareCFourFive();
        prepareBagging();
        methods.setItems(FXCollections.observableArrayList(Arrays.asList(knn, cFourFive, solutionTreeBagging)));
        methods.getSelectionModel().select(0);
        generateAndRecognize();
    }

    private void prepareKNN() {
        knn.setK(KNN_K);
        knn.setTrainSet(patternService.getAll());
    }

    private void prepareCFourFive(){
        cFourFive.setBenchmarks(patterns.getItems());
        cFourFive.setTrainSet(patternService.getAll());
        cFourFive.setAttributesCount(patterns.getSelectionModel().getSelectedItem().getData().length);
        cFourFive.init();
    }

    private void prepareBagging(){
        solutionTreeBagging.setBenchmarks(patterns.getItems());
        solutionTreeBagging.setTrainSet(patternService.getAll());
        solutionTreeBagging.setAttributesCount(patterns.getSelectionModel().getSelectedItem().getData().length);
        solutionTreeBagging.init();
    }


    public void generateAndRecognize() {
        Pattern p = patterns.getSelectionModel().getSelectedItem();
        Pattern distorted = distorter.distort(p, 1, (int) distRate.getValue()).get(0);
        Recognizer recognizer = methods.getValue();
        imagePatternLoader.loadPattern(distorted, data_view);
        imagePatternLoader.loadPattern(recognizer.recognize(distorted), recognized_view);
    }
}
