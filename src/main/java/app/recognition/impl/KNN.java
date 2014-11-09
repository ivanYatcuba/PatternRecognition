package app.recognition.impl;

import app.backend.model.Pattern;
import app.backend.service.PatternService;
import app.recognition.Recognizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class KNN implements Recognizer {

    @Autowired
    private PatternService patternService;

    private int k;
    private List<Pattern> trainSet;

    public KNN() {}

    public KNN(final int k, final List<Pattern> trainSet) {
        this.k = k;
        this.trainSet = trainSet;
    }

    public int getK() {
        return k;
    }

    public void setK(final int k) {
        this.k = k;
    }

    public List<Pattern> getTrainSet() {
        return trainSet;
    }

    public void setTrainSet(final List<Pattern> trainSet) {
        this.trainSet = trainSet;
    }

    @Override
    public Pattern recognize(Pattern input) {
        Map<Pattern, Double> distances = new HashMap<>();
        for(Pattern p : trainSet) {
            distances.put(p, euclideanDistance(input.getData(), p.getData()));
        }
        Map<Long, Integer> patternVotes = new HashMap<>();
        for(int i=0; i<k; i++){
            Pattern closestPattern = null;
            Double minimalDistance =  Collections.min(distances.values());
            for(Pattern candidate: distances.keySet()){
                if(distances.get(candidate) == minimalDistance){
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
                return patternService.findById(p);
            }
        }
        return null;
    }

    private double euclideanDistance(byte[] x, byte[] y){
        int count;
        double distance;
        double sum = 0.0;
        if(x.length != y.length){
            throw new IllegalArgumentException("the number of elements" +
              " in X must match the number of elements in Y");
        }
        else{
            count = x.length;
        }
        for (int i =61; i < count; i++){
            sum = sum + Math.pow(Math.abs(x[i] - y[i]),2);
        }
        distance = Math.sqrt(sum);
        return distance;
    }
    @Override
    public void init(){}

    @Override
    public String toString() {
        return "KNN";
    }
}
