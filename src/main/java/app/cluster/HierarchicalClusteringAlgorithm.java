package app.cluster;

import app.backend.model.Pattern;
import app.cluster.builder.PatternClusterBuilder;
import app.cluster.linkage.Linkage;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class HierarchicalClusteringAlgorithm implements ClusteringAlgorithm {


    private Linkage linkage;
    private PatternClusterBuilder clusterBuilder;
    private MetricMatrixBuilder metricMatrixBuilder;

    public HierarchicalClusteringAlgorithm(Linkage linkage, PatternClusterBuilder clusterBuilder, MetricMatrixBuilder metricMatrixBuilder) {
        this.linkage = linkage;
        this.clusterBuilder = clusterBuilder;
        this.metricMatrixBuilder = metricMatrixBuilder;
    }

    @Override
    public Cluster clusterize(List<Pattern> patterns) {
        if(patterns.size() == 0) {
            throw new IllegalArgumentException();
        }
        List<Cluster> clusters = patterns.stream().map(clusterBuilder::build).collect(Collectors.toList());
        Map<Cluster, Map<Cluster, Double>> distanceMap = metricMatrixBuilder.build(clusters);
        while (distanceMap.keySet().size() > 1) {
            ClusterTuple minimalTuple = findMinimalDistance(distanceMap);
            processMatrix(distanceMap, minimalTuple);
        }
        return distanceMap.keySet().iterator().next();
    }

    private Map<Cluster, Map<Cluster, Double>> processMatrix(Map<Cluster, Map<Cluster, Double>> clusterMap, ClusterTuple clusterTuple) {
        Cluster c1 = clusterTuple.getC1();
        Cluster c2 = clusterTuple.getC2();
        if(!clusterMap.containsKey(c1) || !clusterMap.containsKey(c2)) {
            throw new IllegalArgumentException();
        }
        Cluster mergedCluster = new Cluster();
        mergedCluster.setlNode(c1);
        mergedCluster.setrNode(c2);
        mergedCluster.setDistanceBetweenChildern(clusterTuple.getDistance());
        mergedCluster.setName(c1.getName() + "&" + c2.getName());
        clusterMap.put(mergedCluster, new TreeMap<>());
        for(Cluster cluster: clusterMap.keySet()) {
            if(cluster != c1 && cluster != c2 && cluster != mergedCluster) {
                double d1 = clusterMap.get(cluster).get(c1);
                double d2 = clusterMap.get(cluster).get(c2);
                clusterMap.get(cluster).remove(c1);
                clusterMap.get(cluster).remove(c2);
                double linked = linkage.link(d1, d2);
                clusterMap.get(cluster).put(mergedCluster, linked);
                clusterMap.get(mergedCluster).put(cluster, linked);
            }
        }
        clusterMap.remove(c1);
        clusterMap.remove(c2);
        return clusterMap;
    }

    private ClusterTuple findMinimalDistance( Map<Cluster, Map<Cluster, Double>> clusterMap) {
        double minimal = Double.MAX_VALUE;
        ClusterTuple minimalTuple = null;
        for(Cluster cluster: clusterMap.keySet()) {
            for(Cluster subCluster: clusterMap.get(cluster).keySet()) {
                double current = clusterMap.get(cluster).get(subCluster);
                if(minimal > current) {
                    minimal = current;
                    minimalTuple = new ClusterTuple(cluster, subCluster, current);
                }

            }
        }
        return minimalTuple;
    }

    private class ClusterTuple {
        private Cluster c1;
        private Cluster c2;
        private Double distance;

        public ClusterTuple(Cluster c1, Cluster c2, Double distance) {
            this.c1 = c1;
            this.c2 = c2;
            this.distance = distance;
        }

        public Cluster getC1() {
            return c1;
        }

        public Cluster getC2() {
            return c2;
        }

        public Double getDistance() {
            return distance;
        }
    }


}
