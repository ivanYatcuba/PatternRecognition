package app.recognition;

import app.backend.model.Pattern;
import app.util.pattern.Distorter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ErrorAnalyser {

    Recognizer recognizer;
    List<Pattern> benchmarks;

    public ErrorAnalyser(Recognizer recognizer, List<Pattern> benchmarks) {
        this.recognizer = recognizer;
        this.benchmarks = benchmarks;
    }

    public int analise(int distortionRate) {
        return analise(distortionRate, null, null);
    }

    public int analise(int distortionRate, List<Pattern> trainSet, Map<Pattern, List<Pattern>> testSet) {
        int errorsNum = 0;
        Distorter distorter = new Distorter();
        if(trainSet == null) {
            recognizer.setTrainSet(newTrainSet(benchmarks, distortionRate));
        } else {
            recognizer.setTrainSet(trainSet);
        }

        recognizer.init();
        for (Pattern p : benchmarks) {
            List<Pattern> distortedData;
            if(testSet == null) {
                distortedData = distorter.distort(p, 10, distortionRate);
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
        return errorsNum;
    }

    public static List<Pattern> newTrainSet(List<Pattern> benchmarks, int distortionRate) {
        Distorter distorter = new Distorter();
        List<Pattern> newTrainSet = new ArrayList<>();
        for(Pattern p : benchmarks){
            List<Pattern> distortedData = distorter.distort(p, 10, distortionRate);
            newTrainSet.addAll(distortedData);
        }
        return newTrainSet;
    }

    public static Map<Pattern, List<Pattern>> newTestSet(List<Pattern> benchmarks, int distortionRate) {
        Distorter distorter = new Distorter();
        Map<Pattern, List<Pattern>> train = new HashMap<>();
        for(Pattern p: benchmarks) {
            train.put(p, distorter.distort(p, 10, distortionRate));
        }
        return train;
    }
}
