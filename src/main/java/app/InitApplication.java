package app;



import app.controller.*;
import app.util.*;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class InitApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage stage) throws Exception {
        IController controller = SpringFXMLLoader.load("/fxml/main.fxml");
        Parent parent =  (Parent) controller.getView();

        Scene scene = new Scene(parent);
        stage.setTitle("Pattern Recognition");
        scene.getStylesheets().add("/css/main.css");
        stage.setScene(scene);
        stage.show();
    }
}
