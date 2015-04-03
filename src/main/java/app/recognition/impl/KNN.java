package app.recognition.impl;

import app.backend.model.Pattern;
import app.recognition.Recognizer;

import java.util.*;

public class KNN implements Recognizer {

    private int k;
    private List<Pattern> trainSet;
    private List<Pattern> benchmarks;
    private List<Integer> attributesToIgnore = new ArrayList<>();

    public KNN(int k, List<Pattern> trainSet, List<Pattern> benchmarks) {
        this.k = k;
        this.trainSet = trainSet;
        this.benchmarks = benchmarks;
    }

    @Override
    public Pattern recognize(Pattern input) {
        if(trainSet.size() < 1) {
            return input;
        }
        Map<Pattern, Double> distances = new HashMap<>();
        for(Pattern p : trainSet) {
            distances.put(p, euclideanDistance(input, p));
        }
        Map<Long, Integer> patternVotes = new HashMap<>();
        for(int i=0; i<k; i++){
            Pattern closestPattern = null;
            Double minimalDistance =  Collections.min(distances.values());
            for(Pattern candidate: distances.keySet()){
                if(Objects.equals(distances.get(candidate), minimalDistance)){
                    closestPattern=candidate;break;
                }
            }
            if(!patternVotes.containsKey(closestPattern.getParentId())){
                patternVotes.put(closestPattern.getParentId(), 0);
            } else {
                patternVotes.put(closestPattern.getParentId(), patternVotes.get(closestPattern.getParentId())+1);
            }
            distances.remove(minimalDistance);
        }
        int winner = Collections.max(patternVotes.values());
        for(Long p: patternVotes.keySet()) {
            if(patternVotes.get(p) == winner){
                return retrieveById(p);
            }
        }
        return null;
    }

    private double euclideanDistance(Pattern p1, Pattern p2){
        int count;
        double distance;
        double sum = 0.0;
        if(p1.getImage().getDataSize() != p2.getImage().getDataSize() ){
            throw new IllegalArgumentException("the number of elements" +
              " in X must match the number of elements in Y");
        }
        else{
            count = p1.getImage().getDataSize();
        }
        for (int i =0; i < count; i++){
            if(!attributesToIgnore.contains(i)) {
                int xi = p1.getPixel(i);
                int yi = p2.getPixel(i);
                sum = sum + Math.pow(Math.abs(xi - yi),2);
            }
        }
        distance = Math.sqrt(sum);
        return distance;
    }

    private Pattern retrieveById(long id) {
        for(Pattern p: benchmarks) {
            if(p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public void setTrainSet(final List<Pattern> trainSet) {
        this.trainSet = trainSet;
    }

    @Override
    public List<Pattern> getTrainSet() {
        return trainSet;
    }

    @Override
    public void init(){}

    @Override
    public void setAttributesToIgnore(List<Integer> indexes) {
        attributesToIgnore = indexes;
    }

    @Override
    public String toString() {
        return "KNN";
    }
}
