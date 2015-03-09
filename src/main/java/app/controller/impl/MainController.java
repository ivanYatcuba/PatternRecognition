package app.controller.impl;

import app.controller.FxmlController;
import app.util.SpringFXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.stereotype.Controller;

@Controller
public class MainController extends AbstractFxmlController {

    public void openFirstPart() {
        openPart("/fxml/part1.fxml");
    }

    public   void openSecondPart() {
        openPart("/fxml/part2.fxml");
    }

    private void openPart(String fxmlPath) {
        ((Stage)getView().getScene().getWindow()).close();
        FxmlController controller = SpringFXMLLoader.load(fxmlPath);
        Parent parent = (Parent) controller.getView();
        Scene scene = new Scene(parent);
        final Stage stage = new Stage();
        scene.getStylesheets().add("/css/main.css");
        stage.setScene(scene);
        stage.show();
    }
}
