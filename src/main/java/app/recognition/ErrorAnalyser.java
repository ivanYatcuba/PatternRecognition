package app.recognition;

import app.backend.model.Pattern;
import app.util.pattern.Distorter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ErrorAnalyser {

    private static final int PROTOTYPE_COUNT=10;
    Recognizer recognizer;
    List<Pattern> benchmarks;
    int distortionOffset = 61;

    public void setDistortionOffset(int distortionOffset) {
        this.distortionOffset = distortionOffset;
    }

    public ErrorAnalyser(Recognizer recognizer, List<Pattern> benchmarks) {
        this.recognizer = recognizer;
        this.benchmarks = benchmarks;
    }

    public double analise(int distortionRate) {
        return analise(distortionRate, null, null);
    }

    public double analise(int distortionRate, List<Pattern> trainSet, Map<Pattern, List<Pattern>> testSet) {
        int errorsNum = 0;
        Distorter distorter = new Distorter();
        if(trainSet == null) {
            recognizer.setTrainSet(newTrainSet(benchmarks, distortionRate, distortionOffset));
        } else {
            recognizer.setTrainSet(trainSet);
        }


        recognizer.init();
        for (Pattern p : benchmarks) {
            List<Pattern> distortedData;
            if(testSet == null) {
                distortedData = distorter.distort(p, PROTOTYPE_COUNT, distortionRate, distortionOffset);
            } else {
                distortedData = testSet.get(p);
            }

            for (Pattern distortedPattern : distortedData) {
                Pattern parent = recognizer.recognize(distortedPattern);
                if (p.getId() != parent.getId()) {
                    errorsNum++;
                }
            }
        }
        return (double)errorsNum/(PROTOTYPE_COUNT*benchmarks.size()) *100;
    }

    public static List<Pattern> newTrainSet(List<Pattern> benchmarks, int distortionRate, int distortionOffset) {
        Distorter distorter = new Distorter();
        List<Pattern> newTrainSet = new ArrayList<>();
        for(Pattern p : benchmarks){
            List<Pattern> distortedData = distorter.distort(p, 10, distortionRate, distortionOffset);
            newTrainSet.addAll(distortedData);
        }
        return newTrainSet;
    }

    public static Map<Pattern, List<Pattern>> newTestSet(List<Pattern> benchmarks, int distortionRate, int distortionOffset) {
        Distorter distorter = new Distorter();
        Map<Pattern, List<Pattern>> train = new HashMap<>();
        for(Pattern p: benchmarks) {
            train.put(p, distorter.distort(p, 10, distortionRate, distortionOffset));
        }
        return train;
    }
}
