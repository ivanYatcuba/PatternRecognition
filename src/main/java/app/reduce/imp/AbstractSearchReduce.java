package app.reduce.imp;


import app.backend.model.Pattern;
import app.controller.impl.ProgressController;
import app.recognition.ErrorAnalyser;
import app.recognition.Recognizer;
import app.recognition.impl.KNN;
import app.reduce.Reduce;
import app.util.TestSetFactory;
import app.util.TrainSetFactory;
import javafx.concurrent.Task;
import org.apache.log4j.Logger;

import java.util.*;

public abstract class AbstractSearchReduce extends Task<List<Pattern>> implements Reduce {

    private static final Logger LOG = Logger.getLogger(AbstractSearchReduce.class.getName());
    private static final int PROTOTYPE_PER_PATTERN_SIZE = 10;

    private List<Pattern> benchmarks;
    private int nParam;
    private int distortionRate;
    private boolean isVisualizationEnabled = false;

    private ProgressController progressController;

    List<Integer> results;

    @Override
    public List<Integer> reduce() {
        int benchmarkDataLength = benchmarks.get(0).getImage().getDataSize();
        if (benchmarkDataLength < getParamsCount(benchmarkDataLength, nParam)) {
            throw new IllegalArgumentException("n cannot be greater than num of params");
        }
        Set<Integer> selectedParams = new HashSet<>();
        LOG.debug("Starting reduce..." + new Date());
        TrainSetFactory trainSetFactory = new TrainSetFactory(PROTOTYPE_PER_PATTERN_SIZE);
        List<Pattern> trainSet = trainSetFactory.generateTrainSet(benchmarks, distortionRate);
        TestSetFactory testSetFactory = new TestSetFactory(PROTOTYPE_PER_PATTERN_SIZE);
        Map<Pattern, List<Pattern>> testSet = testSetFactory.newTestSet(benchmarks, distortionRate);

        while (selectedParams.size() < getParamsCount(benchmarkDataLength, nParam)) {
            LOG.debug("Current number of params: " + selectedParams.size());
            LOG.debug("Current params list: " + selectedParams);
            Map<Integer, Double> delMap = new HashMap<>();
            ErrorAnalyser errorAnalyser = new ErrorAnalyser(benchmarks);
            for(int i=0; i < benchmarkDataLength; i++) {
                if (!selectedParams.contains(i)) {
                    delMap.put(i, 0.0);
                    List<Integer> integerList = processResults(selectedParams, benchmarkDataLength, null);
                    if(isVisualizationEnabled) {
                        progressController.changeImage(benchmarks, integerList, i);
                    }
                    Recognizer recognizer = new KNN(3, trainSet, benchmarks);
                    integerList = processResults(selectedParams, benchmarkDataLength, i);
                    recognizer.setAttributesToIgnore(integerList);

                    delMap.put(i, delMap.get(i) + errorAnalyser.analise(recognizer, testSet));

                    LOG.debug("Param: " + i + " gives " + delMap.get(i) + " errors");
                }
                updateMessage(progressMessage() + selectedParams.size() + " Scanning attribute: " + i);
            }
            int newIndex = detectMin(delMap);
            selectedParams.add(newIndex);
            LOG.debug("New param found with index: " + newIndex + "! Error rate: " + delMap.get(newIndex));
            updateProgress(selectedParams.size(), getParamsCount(benchmarkDataLength, nParam));
        }
        updateMessage("Scanning finished!");
        List<Integer> integerList = processResults(selectedParams, benchmarkDataLength, null);
        progressController.changeImage(benchmarks, integerList, null);
        LOG.debug("Reduce finished resulted set: " + selectedParams);
        results = processResults(selectedParams, benchmarkDataLength, null);
        if(nParam <= 0) {
            updateProgress(nParam, nParam);
        }
        succeeded();
        return results;
    }

    @Override
    public List<Integer> getReduceResults() {
        return results;
    }

    protected abstract int getParamsCount(int totalParams, int nParam);

    protected  abstract List<Integer> processResults(Set<Integer> selectedIndex, int totalResults, Integer newIndex);

    protected abstract String progressMessage();

    private Integer detectMin(Map<Integer, Double> map) {
        Double min = Collections.min(map.values());
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
    public void setnParam(int nParam) {
        this.nParam = nParam;
    }

    @Override
    public void setDistortionRate(int distortionRate) {
        this.distortionRate = distortionRate;
    }

    public void setProgressController(ProgressController progressController) {
        this.progressController = progressController;
    }

    @Override
    protected List<Pattern> call() throws Exception {
        reduce();
        return  null;
    }

    public void setVisualizationEnabled(boolean isVisualizationEnabled) {
        this.isVisualizationEnabled = isVisualizationEnabled;
    }
}
