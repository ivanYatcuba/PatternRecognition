package app.cluster.metric;

import app.backend.model.Pattern;

public class ManhattanMetric implements Metric {

    @Override
    public double calculate(Pattern p1, Pattern p2) {
        int count;
        double distance;
        double sum = 0.0;
        if(p1.getImage().getDataSize() != p2.getImage().getDataSize() ){
            throw new IllegalArgumentException("the number of elements" +
                    " in X must match the number of elements in Y");
        }
        else{
            count = p1.getImage().getDataSize();
        }
        for (int i =0; i < count; i++){
            int xi = p1.getPixel(i);
            int yi = p2.getPixel(i);
            sum = sum + Math.abs(xi - yi);
        }
        distance = Math.sqrt(sum);
        return distance;
    }

    @Override
    public String toString() {
        return "Manhattan";
    }
}
