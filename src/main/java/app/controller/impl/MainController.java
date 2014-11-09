package app.controller.impl;

import app.controller.IController;
import javafx.scene.Node;
import org.springframework.stereotype.Controller;

@Controller
public class MainController implements IController {

    private Node view;

    @Override
    public Node getView() {
        return view;
    }

    @Override
    public void setView(Node view) {
        this.view = view;
    }
}
