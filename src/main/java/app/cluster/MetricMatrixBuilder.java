package app.cluster;

import app.cluster.metric.Metric;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MetricMatrixBuilder {

    private Metric metric;

    public Map<Cluster, Map<Cluster, Double>> build(List<Cluster> clusters) {
        Map<Cluster, Map<Cluster, Double>> distanceMap = new TreeMap<>();
        for(final Cluster cluster: clusters) {
            Map<Cluster, Double> concreteClusterDistance = new TreeMap<>();
            clusters.stream().filter(subCluster -> cluster != subCluster).forEach(subCluster -> {
                double distance = metric.calculate(cluster.getData(), subCluster.getData());
                concreteClusterDistance.put(subCluster, distance);
            });
            distanceMap.put(cluster, concreteClusterDistance);
        }
        return distanceMap;
    }

    public Metric getMetric() {
        return metric;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }
}
