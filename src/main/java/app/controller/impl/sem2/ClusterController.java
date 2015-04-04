package app.controller.impl.sem2;

import app.backend.service.PatternService;
import app.cluster.*;
import app.cluster.builder.PatternClusterBuilder;
import app.cluster.linkage.AverageLinkage;
import app.cluster.linkage.CompleteLinkage;
import app.cluster.linkage.Linkage;
import app.cluster.linkage.SingleLinkage;
import app.cluster.metric.EuclideanMetric;
import app.cluster.metric.Metric;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class ClusterController implements Initializable {

    @FXML
    private ComboBox<Metric> metricComboBox;
    @FXML
    private ComboBox<Linkage> linkageComboBox;
    @FXML
    private SwingNode dendrogram;
    @FXML
    private StackPane setContent;
    @FXML
    private Button start;

    @Autowired
    private PatternService patternService;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        metricComboBox.setItems(FXCollections.observableArrayList(new EuclideanMetric()));
        linkageComboBox.setItems(FXCollections.observableArrayList(new SingleLinkage(), new AverageLinkage(), new CompleteLinkage()));
        metricComboBox.getSelectionModel().select(0);
        linkageComboBox.getSelectionModel().select(0);
    }

    public void startCluster() throws InterruptedException {
        PatternClusterBuilder clusterBuilder = new PatternClusterBuilder();
        MetricMatrixBuilder metricMatrixBuilder = new MetricMatrixBuilder();
        metricMatrixBuilder.setMetric(metricComboBox.getValue());
        ClusteringAlgorithm algorithm = new HierarchicalClusteringAlgorithm(linkageComboBox.getValue(),
                clusterBuilder, metricMatrixBuilder);
        Cluster cluster = algorithm.clusterize(patternService.getAll());

        SwingNode swingNode = new SwingNode();
        swingNode.setContent(new DendrogramPaintPanel(cluster));
        setContent.getChildren().add(swingNode);
        Thread.sleep(1000);
    }
}
