package app.controller.impl.sem2;

import app.controller.impl.AbstractFxmlController;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class DendragramController extends AbstractFxmlController implements Initializable {

    @FXML
    private SwingNode node;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
