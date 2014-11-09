package app.recognition.impl;

import app.backend.model.Pattern;
import app.recognition.Recognizer;
import app.util.BitUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SolutionTreeBagging implements Recognizer {
    private static final int SUBSET_SIZE = 70;
    private static final int CLASSIFIER_COUNT = 5;
    private List<Pattern> benchmarks;
    private List<Pattern> trainSet;
    private int attributesCount;

    private List<CFourFive> forest;

    @Autowired
    private BitUtil bitUtil;

    @Override
    public Pattern recognize(final Pattern input) {
        Map<Pattern, Integer> votes = new HashMap<>();
        for(CFourFive cFourFive: forest){
            Pattern p = cFourFive.recognize(input);
            if(!votes.containsKey(p)){
                votes.put(p, 1);
            }else {
                votes.put(p, votes.get(p)+1);
            }
        }
        int maxVotes = Collections.max(votes.values());
        Pattern winner = null;
        for(Pattern candidate: votes.keySet()){
            if(votes.get(candidate) == maxVotes){
                winner=candidate;break;
            }
        }
        return winner;
    }
    @Override
    public void init(){
        forest = new ArrayList<>();
        for(int i=0; i<CLASSIFIER_COUNT; i++){
            CFourFive cFourFive = new CFourFive();
            cFourFive.setBenchmarks(benchmarks);
            cFourFive.setTrainSet(generateSubList(trainSet));
            cFourFive.setAttributesCount(attributesCount);
            cFourFive.setBitUtil(bitUtil);
            cFourFive.init();
            forest.add(cFourFive);
        }

    }

    private List<Pattern> generateSubList(List<Pattern> originalList){
        List<Pattern> patterns = new ArrayList<>();
        Random rand = new Random();
        for(Pattern p: originalList){
            if(rand.nextInt((100) + 1) <= SUBSET_SIZE){
                patterns.add(p);
            }
        }
        return patterns;
    }

    public List<Pattern> getBenchmarks() {
        return benchmarks;
    }

    public void setBenchmarks(final List<Pattern> benchmarks) {
        this.benchmarks = benchmarks;
    }

    public List<Pattern> getTrainSet() {
        return trainSet;
    }

    public void setTrainSet(final List<Pattern> trainSet) {
        this.trainSet = trainSet;
    }

    public int getAttributesCount() {
        return attributesCount;
    }

    public void setAttributesCount(final int attributesCount) {
        this.attributesCount = attributesCount;
    }

    @Override
    public String toString() {
        return "Solution Tree Bagging";
    }
}
