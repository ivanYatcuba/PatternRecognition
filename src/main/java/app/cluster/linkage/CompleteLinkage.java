package app.cluster.linkage;

public class CompleteLinkage implements Linkage {
    @Override
    public double link(double d1, double d2) {
        return Math.max(d1, d2);
    }

    @Override
    public String toString() {
        return "Complete Linkage";
    }
}
