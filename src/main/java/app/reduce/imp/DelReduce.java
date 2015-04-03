package app.reduce.imp;


import java.util.*;

public class DelReduce extends AbstractSearchReduce {
    @Override
    protected int getParamsCount(int totalParams, int nParam) {
        return nParam;
    }

    @Override
    protected List<Integer> processResults(Set<Integer> selectedIndex, int totalResults, Integer index) {
        ArrayList<Integer> params = new ArrayList<>(selectedIndex);
        if(index != null) {
            params.add(index);
        }
        return params;
    }

    @Override
    protected String progressMessage() {
        return "Attributes removed: ";
    }


    @Override
    public String toString() {
        return "Del";
    }
}
