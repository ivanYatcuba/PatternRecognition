package app.reduce.imp;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AddReduce extends AbstractSearchReduce {


    @Override
    protected int getParamsCount(int totalParams, int nParam) {
        return totalParams - nParam;
    }

    @Override
    protected List<Integer> processResults(Set<Integer> selectedIndex, int totalResults, Integer index) {
        ArrayList<Integer> params = new ArrayList<>();
        for(int i=0; i < totalResults; i++) {
            params.add(i);
        }
        params.removeAll(selectedIndex);
        if(index != null) {
            params.remove(index);
        }

        return params;
    }

    @Override
    protected String progressMessage() {
        return "Attributes added: ";
    }


    @Override
    public String toString() {
        return "Add";
    }

}
