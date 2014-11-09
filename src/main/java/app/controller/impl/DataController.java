package app.controller.impl;

import app.backend.model.Pattern;
import app.backend.service.PatternService;
import app.controller.IController;
import app.util.Distorter;
import app.util.ImagePatternLoader;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class DataController implements IController, Initializable {

    @Autowired
    private PatternService patternService;
    @Autowired
    private ImagePatternLoader imagePatternLoader;
    @Autowired
    private Distorter distorter;

    private Node view;

    final FileChooser fileChooser = new FileChooser();

    @FXML
    private ImageView img;

    @FXML
    private ComboBox<Pattern> patterns;

    @FXML
    private ListView<Pattern> distortedPatterns;

    @FXML
    private TextField number;

    @FXML
    private Slider distRate;


    @Override
    public Node getView() {
        return view;
    }

    @Override
    public void setView(Node view) {
        this.view = view;
    }

    public void createNew() {
        File file = fileChooser.showOpenDialog(view.getScene().getWindow());
        if (file != null) {
            img.setImage(new Image(file.toURI().toString()));

            Pattern pattern = new Pattern();
            pattern.setBenchmark(true);
            pattern.setName(file.getName());
            File imgPath = new File(file.getPath());
            BufferedImage bufferedImage;
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
                bufferedImage = ImageIO.read(imgPath);
                ImageIO.write(bufferedImage, "bmp", baos);
                baos.flush();

                pattern.setData(baos.toByteArray());
                baos.close();

                patternService.save(pattern);

                patterns.getItems().add(pattern);
                patterns.getSelectionModel().select(pattern);
            } catch (IOException e) {
                System.out.println("Error =(");
            }
        }


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        img.setImage(new Image("img/empty.jpg"));
        patterns.valueProperty().addListener(new ChangeListener<Pattern>() {
            @Override
            public void changed(ObservableValue<? extends Pattern> observable, Pattern oldValue, Pattern newValue) {
                if (newValue != null) {
                    imagePatternLoader.loadPattern(newValue, img);
                    try {
                        distortedPatterns.setItems(
                          FXCollections.observableArrayList(patternService.getChildrenPatterns(newValue.getId())));
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        distortedPatterns.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Pattern>() {
            @Override
            public void changed(ObservableValue<? extends Pattern> observable, Pattern oldValue, Pattern newValue) {
                if (newValue != null) {
                    imagePatternLoader.loadPattern(newValue, img);
                }
            }
        });
        number.setText("0");
        number.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(
              final ObservableValue<? extends String> observable,
              final String oldValue,
              final String newValue) {
                if (!newValue.matches("^$|[0-9]+")) { number.setText(oldValue); }
            }
        });
        patterns.setItems(FXCollections.observableArrayList(patternService.getBenchmarks()));
        patterns.getSelectionModel().select(0);
    }

    public void distort() {
        try {
            int num = Integer.parseInt(number.getText());
            Pattern p = patterns.getSelectionModel().getSelectedItem();
            List<Pattern> distortedData = distorter.distort(p, num, (int) distRate.getValue());
            patternService.removeDistorted(p.getId());
            for (Pattern dp : distortedData) {
                patternService.save(dp);
            }
            distortedPatterns.setItems(FXCollections.observableArrayList(distortedData));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}