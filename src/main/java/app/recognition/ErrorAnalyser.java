package app.recognition;

import app.backend.model.Pattern;
import app.util.pattern.Distorter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ErrorAnalyser {

    private List<Pattern> benchmarks;

    public ErrorAnalyser(List<Pattern> benchmarks) {
        this.benchmarks = benchmarks;
    }

    public double analise(Recognizer recognizer, Map<Pattern, List<Pattern>> testSet) {
        if(recognizer.getTrainSet() == null || testSet == null) {
            throw new IllegalArgumentException();
        }
        int errorsNum = 0;
        recognizer.init();
        for (Pattern p : benchmarks) {
            List<Pattern> distortedData = testSet.get(p);
            for (Pattern distortedPattern : distortedData) {
                Pattern parent = recognizer.recognize(distortedPattern);
                if (p.getId() != parent.getId()) {
                    errorsNum++;
                }
            }
        }
        return (double)errorsNum/(testSet.get(testSet.keySet().iterator().next()).size()*benchmarks.size()) *100;
    }
}
