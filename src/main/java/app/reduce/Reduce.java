package app.reduce;

import app.backend.model.Pattern;
import app.util.ByteUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface Reduce {
    void setBenchmarks(List<Pattern> benchmarks);
    void setSizeOfNewParamList(int sizeOfNewParamList);
    void setDistortionRate(int distortionRate);
    List<Integer> reduce ();
    List<Integer> getReduceResults();
    void setTrainSet(List<Pattern> trainSet);

    static List<Pattern> reduceTrainSet(List<Pattern> trainSet, List<Integer> indexToRemove) {
        return trainSet.stream().map(p -> p.copy(ByteUtil.removeListOfIndexes(p.getBitData(), indexToRemove))).collect(Collectors.toList());
    }

    static Map<Pattern, List<Pattern>> reduceTestSet(Map<Pattern, List<Pattern>> testSet, List<Integer> indexToRemove) {
        Map<Pattern, List<Pattern>> reducedTestSet = new HashMap<>();
        for(Pattern parent: testSet.keySet()) {
            Pattern newParent = parent.copy(ByteUtil.removeListOfIndexes(parent.getBitData(), indexToRemove));
            reducedTestSet.put(newParent, reduceTrainSet(testSet.get(parent), indexToRemove));
        }
        return reducedTestSet;
    }
}
