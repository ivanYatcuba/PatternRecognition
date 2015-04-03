package app.util;

import app.backend.model.Pattern;
import app.util.pattern.Distorter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestSetFactory {

    private int prototypePerPatternSize;

    public TestSetFactory(int prototypePerPatternSize) {
        this.prototypePerPatternSize = prototypePerPatternSize;
    }


    public Map<Pattern, List<Pattern>> newTestSet(List<Pattern> benchmarks, int distortionRate) {
        Distorter distorter = new Distorter();
        Map<Pattern, List<Pattern>> train = new HashMap<>();
        for(Pattern p: benchmarks) {
            train.put(p, distorter.distort(p, prototypePerPatternSize, distortionRate));
        }
        return train;
    }
}
