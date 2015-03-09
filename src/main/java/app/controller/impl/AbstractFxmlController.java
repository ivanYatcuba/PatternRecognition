package app.controller.impl;

import app.controller.FxmlController;
import javafx.scene.Node;

public class AbstractFxmlController implements FxmlController {
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
