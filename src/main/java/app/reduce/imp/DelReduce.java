package app.reduce.imp;

import app.backend.model.Pattern;
import app.recognition.ErrorAnalyser;
import app.recognition.impl.KNN;
import app.reduce.Reduce;
import app.util.ByteUtil;
import javafx.concurrent.Task;

import java.util.*;
import java.util.stream.Collectors;

public class DelReduce extends AbstractSearchReduce {

    @Override
    protected List<Pattern> getModifiedBenchmarks(List<Pattern> benchmarks, Set<Integer> oldParams, int removeParam) {
        ArrayList<Integer> params = new ArrayList<>(oldParams);
        params.add(removeParam);
        return benchmarks.stream().map(p -> p.copy(ByteUtil.removeListOfIndexes(p.getBitData(), new ArrayList<>(params)))).collect(Collectors.toList());
    }
}
