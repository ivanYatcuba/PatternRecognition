package app.reduce.imp;


import app.backend.model.Pattern;
import app.recognition.ErrorAnalyser;
import app.recognition.impl.KNN;
import app.reduce.Reduce;
import javafx.concurrent.Task;

import java.util.*;

public abstract class AbstractSearchReduce extends Task<List<Pattern>> implements Reduce {

    private List<Pattern> benchmarks;
    private List<Pattern> trainSet;
    private int sizeOfNewParamList;
    private int distortionRate;

    List<Integer> results;

    @Override
    public List<Integer> reduce() {
        int benchmarkDataLength = benchmarks.get(0).getData().length;
        if (benchmarkDataLength < sizeOfNewParamList) {
            throw new IllegalArgumentException("n cannot be greater than num of params");
        }
        Set<Integer> paramsToRemoveIndexes = new HashSet<>();
        while (paramsToRemoveIndexes.size() < sizeOfNewParamList && sizeOfNewParamList > 0) {
            Map<Integer, Integer> delMap = new HashMap<>();
            for(int i=0; i < benchmarkDataLength; i++) {
                if (!paramsToRemoveIndexes.contains(i)) {
                    delMap.put(i, 0);
                    List<Pattern> modifiedBenchmarks = getModifiedBenchmarks(benchmarks, paramsToRemoveIndexes, i);
                    List<Pattern> newTrainSet = Reduce.reduceTrainSet(trainSet, new ArrayList<>(paramsToRemoveIndexes));
                    ErrorAnalyser errorAnalyser = new ErrorAnalyser(new KNN(3, newTrainSet, modifiedBenchmarks), modifiedBenchmarks);
                    errorAnalyser.setDistortionOffset(0);
                    delMap.put(i, delMap.get(i) + errorAnalyser.analise(distortionRate));
                }
                updateMessage("Attributes removed: " + paramsToRemoveIndexes.size() + " Scanning attribute: " + i);
            }
            paramsToRemoveIndexes.add(detectMin(delMap));
            updateProgress(paramsToRemoveIndexes.size(), sizeOfNewParamList);
        }
        if(sizeOfNewParamList <= 0) {
            updateProgress(sizeOfNewParamList, sizeOfNewParamList);
        }
        results = new ArrayList<>(paramsToRemoveIndexes);
        return results;
    }

    @Override
    public List<Integer> getReduceResults() {
        return results;
    }

    @Override
    public void setTrainSet(List<Pattern> trainSet) {
        this.trainSet = trainSet;
    }

    protected abstract List<Pattern> getModifiedBenchmarks(List<Pattern> benchmarks, Set<Integer> oldParams, int newParam);

    private Integer detectMin(Map<Integer, Integer> map) {
        Integer min = Collections.min(map.values());
        for(int i: map.keySet()) {
            if(Objects.equals(map.get(i), min)) {
                return i;
            }
        }
        throw new ArrayIndexOutOfBoundsException("Error finding min value");
    }

    @Override
    public void setBenchmarks(List<Pattern> benchmarks) {
        this.benchmarks = benchmarks;
    }

    @Override
    public void setSizeOfNewParamList(int sizeOfNewParamList) {
        this.sizeOfNewParamList = sizeOfNewParamList;
    }

    @Override
    public void setDistortionRate(int distortionRate) {
        this.distortionRate = distortionRate;
    }

    @Override
    public String toString() {
        return "Del";
    }

    @Override
    protected List<Pattern> call() throws Exception {
        reduce();
        return  null;
    }
}
