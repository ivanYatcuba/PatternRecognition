package app.reduce.imp;


import app.backend.model.Pattern;
import app.recognition.ErrorAnalyser;
import app.recognition.impl.KNN;
import app.reduce.Reduce;
import javafx.concurrent.Task;
import org.apache.log4j.Logger;

import java.util.*;

public abstract class AbstractSearchReduce extends Task<List<Pattern>> implements Reduce {

    private static final Logger LOG = Logger.getLogger(AbstractSearchReduce.class.getName());

    private List<Pattern> benchmarks;
    private List<Pattern> trainSet;
    private int sizeOfNewParamList;
    private int distortionRate;

    List<Integer> results;

    @Override
    public List<Integer> reduce() {
        int benchmarkDataLength = benchmarks.get(0).getData().length * 8;
        if (benchmarkDataLength < sizeOfNewParamList) {
            throw new IllegalArgumentException("n cannot be greater than num of params");
        }
        Set<Integer> paramsToRemoveIndexes = new HashSet<>();
        LOG.debug("Starting reduce...");
        while (paramsToRemoveIndexes.size() < sizeOfNewParamList && sizeOfNewParamList > 0) {
            LOG.debug("Current number of params: " + paramsToRemoveIndexes.size());
            LOG.debug("Current params list: " + paramsToRemoveIndexes);
            Map<Integer, Integer> delMap = new HashMap<>();
            for(int i=0; i < benchmarkDataLength; i++) {
                if (!paramsToRemoveIndexes.contains(i)) {
                    delMap.put(i, 0);
                    List<Pattern> modifiedBenchmarks = getModifiedBenchmarks(benchmarks, paramsToRemoveIndexes, i);
                    List<Pattern> newTrainSet = Reduce.reduceTrainSet(trainSet, new ArrayList<>(paramsToRemoveIndexes));
                    ErrorAnalyser errorAnalyser = new ErrorAnalyser(new KNN(3, newTrainSet, modifiedBenchmarks), modifiedBenchmarks);
                    errorAnalyser.setDistortionOffset(0);
                    delMap.put(i, delMap.get(i) + errorAnalyser.analise(distortionRate));
                    LOG.debug("Param: " + i + " gives " + delMap.get(i) + " errors");
                }
                updateMessage("Attributes removed: " + paramsToRemoveIndexes.size() + " Scanning attribute: " + i);
            }
            int newIndex = detectMin(delMap);
            paramsToRemoveIndexes.add(newIndex);
            LOG.debug("New param found with index: " + newIndex + "! Error rate: " + delMap.get(newIndex));
            updateProgress(paramsToRemoveIndexes.size(), sizeOfNewParamList);
        }
        if(sizeOfNewParamList <= 0) {
            updateProgress(sizeOfNewParamList, sizeOfNewParamList);
        }
        LOG.debug("Reduce finished resulted set: " + paramsToRemoveIndexes);
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
