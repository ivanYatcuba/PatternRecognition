package app.cluster.linkage;

public class AverageLinkage implements Linkage {
    @Override
    public double link(double d1, double d2) {
        return (d1+d2)/2;
    }

    @Override
    public String toString() {
        return "Average Linkage";
    }
}
