package app.cluster;

import app.backend.model.Pattern;

import java.util.List;

public interface ClusteringAlgorithm {

    public Cluster clusterize(List<Pattern> patterns);
}
