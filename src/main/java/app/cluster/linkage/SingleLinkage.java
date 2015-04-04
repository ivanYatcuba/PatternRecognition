package app.cluster.linkage;

public class SingleLinkage implements Linkage {

    @Override
    public double link(double d1, double d2) {
        return Math.min(d1, d2);
    }

    @Override
    public String toString() {
        return "Single Linkage";
    }
}
