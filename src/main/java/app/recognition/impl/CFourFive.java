package app.recognition.impl;

import app.backend.model.Pattern;
import app.recognition.Recognizer;
import app.util.AbstractNode;
import app.util.BTree;
import app.util.BitUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CFourFive implements Recognizer {

    @Autowired
    private BitUtil bitUtil;

    private List<Pattern> benchmarks;
    private List<Pattern> trainSet;
    private int attributesCount;

    private BTree<Pattern> classificationTree;

    @Override
    public Pattern recognize(final Pattern input) {
        return recognizeR((ClassificationNode<Pattern>)classificationTree.getRoot(), input);
    }

    private Pattern recognizeR(ClassificationNode<Pattern> currentNode, Pattern pattern){
        if(currentNode.getData() != null){
            return  currentNode.getData();
        }
        if(bitUtil.bitIsSet(pattern.getData()[currentNode.getByteId()], currentNode.getBitId())){
            return recognizeR((ClassificationNode<Pattern>)currentNode.getrNode(), pattern);
        }else {
            return recognizeR((ClassificationNode<Pattern>)currentNode.getlNode(), pattern);
        }
    }

    @Override
    public void init(){
        classificationTree = new BTree(new ClassificationNode<Pattern>());
        buildTreeR((ClassificationNode<Pattern>)classificationTree.getRoot(), trainSet);
    }

    private void buildTreeR(ClassificationNode<Pattern> currentNode, List<Pattern> currentSet){
        for(Pattern p: benchmarks) {
            if(getFrequency(p, currentSet) == currentSet.size()){
                currentNode.setData(p);return;
            }
        }
        Map<Double, Long> gainMap = new HashMap<>();
        Map<Long, ListTuple<Pattern>> attributeLists = new HashMap<>();
        double info = getInfo(currentSet);
        for(int i = 62; i < attributesCount; i++){
            for(int j=0; j<8; j++){
                List<Pattern> lList = new ArrayList<>();
                List<Pattern> rList = new ArrayList<>();
                for(Pattern p: currentSet) {
                    if(bitUtil.bitIsSet(p.getData()[i], j)){rList.add(p);}
                    else{lList.add(p);}
                }
                long attributeIndex = (long)(i*10+j);
                attributeLists.put(attributeIndex, new ListTuple<>(lList, rList));
                gainMap.put(info - getInfoX(Arrays.asList(lList, rList), currentSet.size()), attributeIndex);
            }
        }
        double maxGain = Collections.max(gainMap.keySet());
        final long paramId = gainMap.get(maxGain);
        currentNode.setByteBitId((int)paramId/10, (int)paramId%10);
        currentNode.setrNode(new ClassificationNode<Pattern>());
        currentNode.setlNode(new ClassificationNode<Pattern>());
        if(attributeLists.get(paramId).getrList().size() != 0){
            buildTreeR((ClassificationNode<Pattern>)currentNode.getrNode(), attributeLists.get(paramId).getrList());
        }
        if(attributeLists.get(paramId).getlList().size() != 0){
            buildTreeR((ClassificationNode<Pattern>)currentNode.getlNode(), attributeLists.get(paramId).getlList());
        }
    }

    private int getFrequency(Pattern benchmark, List<Pattern> patternList){
        int frequency = 0;
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

    private class ClassificationNode<T> extends AbstractNode<Pattern> {
       private int byteId;
       private int bitId;

        public void setByteBitId(int byteId, int bitId){
            this.byteId = byteId;
            this.bitId = bitId;
        }

        public int getByteId() {return byteId;}
        public int getBitId() {return bitId;}
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

        public void setlList(final List<T> lList) {
            this.lList = lList;
        }

        public List<T> getrList() {
            return rList;
        }

        public void setrList(final List<T> rList) {
            this.rList = rList;
        }
    }

    public void setBenchmarks(final List<Pattern> benchmarks) {
        this.benchmarks = benchmarks;
    }

    public void setTrainSet(final List<Pattern> trainSet) {
        this.trainSet = trainSet;
    }

    public void setAttributesCount(final int attributesCount) {
        this.attributesCount = attributesCount;
    }

    public BitUtil getBitUtil() {
        return bitUtil;
    }

    public void setBitUtil(final BitUtil bitUtil) {
        this.bitUtil = bitUtil;
    }

    @Override
    public String toString() {
        return "C4.5";
    }
}
