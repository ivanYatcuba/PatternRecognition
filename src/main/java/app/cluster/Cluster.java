package app.cluster;

import app.backend.model.Pattern;
import app.util.tree.AbstractNode;

import java.util.ArrayList;
import java.util.List;

public class Cluster extends AbstractNode<Pattern> implements Comparable<Cluster> {

    private String name;
    private Double distanceBetweenChildren;

    public boolean isLeaf() {
      return this.data != null && this.getlNode() == null && this.getrNode() == null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Cluster> getChildren() {
        List<Cluster> clusters = new ArrayList<>();
        if(this.getlNode() != null) {
            clusters.add((Cluster)this.getlNode());
        }
        if(this.getrNode() != null) {
            clusters.add((Cluster)this.getrNode());
        }
        return clusters;
    }

    public Double getDistanceBetweenChildern() {
        return distanceBetweenChildren;
    }

    public void setDistanceBetweenChildern(Double distanceBetweenChildren) {
        this.distanceBetweenChildren = distanceBetweenChildren;
    }

    @Override
    public int compareTo(Cluster o) {
        return this.getName().compareTo(o.getName());
    }
}
