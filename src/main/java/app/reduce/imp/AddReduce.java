package app.reduce.imp;

import app.backend.model.Pattern;
import app.util.ByteUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AddReduce extends AbstractSearchReduce {

    @Override
    protected List<Pattern> getModifiedBenchmarks(List<Pattern> benchmarks, Set<Integer> oldParams, int newParam) {
        ArrayList<Integer> params = new ArrayList<>();
        for(int i=0; i<benchmarks.get(0).getBitData().length; i++) {
            params.add(i);
        }
        params.removeAll(oldParams);
        params.remove(new Integer(newParam));
        return benchmarks.stream().map(p -> p.copy(ByteUtil.removeListOfIndexes(p.getBitData(), new ArrayList<>(params)))).collect(Collectors.toList());
    }
}
