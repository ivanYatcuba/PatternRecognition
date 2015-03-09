package app.recognition;

import app.backend.model.Pattern;
import app.util.pattern.Distorter;

import java.util.ArrayList;
import java.util.List;

public class ErrorAnalyser {

    Recognizer recognizer;
    List<Pattern> benchmarks;

    public ErrorAnalyser(Recognizer recognizer, List<Pattern> benchmarks) {
        this.recognizer = recognizer;
        this.benchmarks = benchmarks;
    }

    public int analise(int distortionRate) {
        int errorsNum = 0;
        List<Pattern> newTrainSet = new ArrayList<>();
        Distorter distorter = new Distorter();
        for(Pattern p : benchmarks){
            List<Pattern> distortedData = distorter.distort(p, 10, distortionRate);
            newTrainSet.addAll(distortedData);
        }
        recognizer.setTrainSet(newTrainSet);
        recognizer.init();
        for (Pattern p : benchmarks) {
            List<Pattern> distortedData = distorter.distort(p, 10, distortionRate);
            for (Pattern distortedPattern : distortedData) {
                Pattern parent = recognizer.recognize(distortedPattern);
                if (p.getId() != parent.getId()) {
                    errorsNum++;
                }
            }
        }
        return errorsNum;
    }
}
