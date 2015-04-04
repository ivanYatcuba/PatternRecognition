package app.cluster.builder;

import app.cluster.Cluster;

public interface ClusterBuilder<T> {

    public Cluster build(T t);
}
