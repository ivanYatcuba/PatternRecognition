package app.recognition.impl;

import app.backend.model.Pattern;
import app.recognition.Recognizer;

import java.util.*;
import java.util.stream.Collectors;

public class SolutionTreeBagging implements Recognizer {

    private static final int SUBSET_SIZE = 70;
    private static final int CLASSIFIER_COUNT = 5;

    private List<Pattern> benchmarks;
    private List<Pattern> trainSet;
    private List<CFourFive> forest;

    private int attributesCount;

    public SolutionTreeBagging(List<Pattern> benchmarks, List<Pattern> trainSet, int attributesCount) {
        this.benchmarks = benchmarks;
        this.trainSet = trainSet;
        this.attributesCount = attributesCount;
    }

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
            CFourFive cFourFive = new CFourFive(benchmarks, generateSubList(trainSet), attributesCount);
            cFourFive.init();
            forest.add(cFourFive);
        }

    }

    private List<Pattern> generateSubList(List<Pattern> originalList){
        List<Pattern> patterns = new ArrayList<>();
        Random rand = new Random();
        patterns.addAll(originalList.stream().filter(p -> rand.nextInt((100) + 1) <= SUBSET_SIZE).collect(Collectors.toList()));
        return patterns;
    }

    public void setTrainSet(final List<Pattern> trainSet) {
        this.trainSet = trainSet;
    }

    @Override
    public String toString() {
        return "Solution Tree Bagging";
    }
}
