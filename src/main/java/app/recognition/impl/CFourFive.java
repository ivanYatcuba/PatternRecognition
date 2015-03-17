package app.recognition.impl;

import app.backend.model.Pattern;
import app.recognition.Recognizer;
import app.util.tree.AbstractNode;
import app.util.tree.BTree;
import app.util.pattern.BitUtil;

import java.util.*;

public class CFourFive implements Recognizer {

    private List<Pattern> benchmarks;
    private List<Pattern> trainSet;
    private int attributesCount;

    private BTree<Pattern> classificationTree;

    private int frequencyOffset = 0;

    public CFourFive(List<Pattern> benchmarks, List<Pattern> trainSet, int attributesCount) {
        this.benchmarks = benchmarks;
        this.trainSet = trainSet;
        this.attributesCount = attributesCount;
    }

    @Override
    public Pattern recognize(final Pattern input) {
        return recognizeR((ClassificationNode)classificationTree.getRoot(), input);
    }

    private Pattern recognizeR(ClassificationNode currentNode, Pattern pattern){
        if(currentNode.getData() != null){
            return  currentNode.getData();
        }
        if(pattern.getBitData()[currentNode.bitId]){
            return recognizeR((ClassificationNode)currentNode.getrNode(), pattern);
        }else {
            return recognizeR((ClassificationNode)currentNode.getlNode(), pattern);
        }
    }

    @Override
    public void init(){
        classificationTree = new BTree(new ClassificationNode());
        if(trainSet.size() > attributesCount) {
            frequencyOffset = trainSet.size() - (attributesCount );
        }

        buildTreeR((ClassificationNode) classificationTree.getRoot(), trainSet);
    }

    private ClassificationNode buildTreeR(ClassificationNode currentNode, List<Pattern> currentSet){
        for(Pattern p: benchmarks) {
            if(getFrequency(p, currentSet) >= currentSet.size()){
                currentNode.setData(p);
                return currentNode;
            }
        }
        Map<Double, Long> gainMap = new HashMap<>();
        Map<Long, ListTuple<Pattern>> attributeLists = new HashMap<>();
        double info = getInfo(currentSet);
        for(int i = 0; i < attributesCount; i++){
                List<Pattern> lList = new ArrayList<>();
                List<Pattern> rList = new ArrayList<>();
                for(Pattern p: currentSet) {
                    if(p.getBitData()[i]){rList.add(p);}
                    else{lList.add(p);}
                }
                long attributeIndex = (long)(i);
                attributeLists.put(attributeIndex, new ListTuple<>(lList, rList));
                gainMap.put(info - getInfoX(Arrays.asList(lList, rList), currentSet.size()), attributeIndex);
        }
        double maxGain = Collections.max(gainMap.keySet());
        if(maxGain == 0) {
            for(Pattern p: benchmarks) {
                if(p.getId() == currentSet.get(0).getParentId()){
                    currentNode.setData(p);
                    return currentNode;
                }
            }
        }
        final long paramId = gainMap.get(maxGain);
        currentNode.setByteBitId((int)paramId);

        if(attributeLists.get(paramId).getrList().size() != 0) {
            currentNode.setrNode(new ClassificationNode());
            buildTreeR((ClassificationNode)currentNode.getrNode(), attributeLists.get(paramId).getrList());
        }
        if(attributeLists.get(paramId).getlList().size() != 0) {
            currentNode.setlNode(new ClassificationNode());
            buildTreeR((ClassificationNode) currentNode.getlNode(), attributeLists.get(paramId).getlList());
        }
        return new ClassificationNode();
    }

    private int getFrequency(Pattern benchmark, List<Pattern> patternList){
        int frequency = frequencyOffset;
        for(Pattern p: patternList) {
            if(p.getParentId() == benchmark.getId()){frequency++;}
        }
        return frequency;
    }
    private double getInfo(List<Pattern> patternList){
        double result = 0;
        for(Pattern benchmark: benchmarks) {
            double patternProbability = (double)getFrequency(benchmark, patternList)/(double)patternList.size();
            if(patternProbability!=0){
                result +=(patternProbability)*(Math.log(patternProbability)/Math.log(2.0d));
            }
        }
        return result*(-1);
    }
    private double getInfoX(List<List<Pattern>> patternSubList, int listSize){
        double result = 0;
        for(List<Pattern> subset: patternSubList){
            if(subset.size()!=0){
                result += ((double)subset.size()/(double)listSize)*getInfo(subset);
            }
        }
        return result;
    }

    private class ClassificationNode extends AbstractNode<Pattern> {
       private int bitId;

        public void setByteBitId(int bitId){
            this.bitId = bitId;
        }
    }


    private class ListTuple<T> {
        private List<T> lList;
        private List<T> rList;

        private ListTuple(final List<T> lList, final List<T> rList) {
            this.lList = lList;
            this.rList = rList;
        }

        public List<T> getlList() {
            return lList;
        }

        public List<T> getrList() {
            return rList;
        }
    }

    @Override
    public void setTrainSet(final List<Pattern> trainSet) {
        this.trainSet = trainSet;
    }

    @Override
    public String toString() {
        return "C4.5";
    }
}
