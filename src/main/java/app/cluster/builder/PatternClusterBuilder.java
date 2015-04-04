package app.cluster.builder;

import app.backend.model.Pattern;
import app.cluster.Cluster;

public class PatternClusterBuilder implements ClusterBuilder<Pattern> {

    public Cluster build(Pattern pattern) {
        Cluster cluster = new Cluster();
        cluster.setData(pattern);
        cluster.setName(pattern.getName());
        return cluster;
    }
}
