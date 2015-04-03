package app.util;


import app.backend.model.Pattern;
import app.util.pattern.Distorter;

import java.util.ArrayList;
import java.util.List;

public class TrainSetFactory {

    private int prototypePerPatternSize;

    public TrainSetFactory(int prototypePerPatternSize) {
        this.prototypePerPatternSize = prototypePerPatternSize;
    }

    public List<Pattern> generateTrainSet(List<Pattern> benchmarks, int distRate) {
        List<Pattern> trainSet = new ArrayList<>();
        for(Pattern p: benchmarks) {
            Distorter distorter = new Distorter();
            trainSet.addAll(distorter.distort(p, prototypePerPatternSize, distRate));
        }
        return trainSet;
    }

}
