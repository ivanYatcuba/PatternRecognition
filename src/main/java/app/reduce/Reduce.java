package app.reduce;

import app.backend.model.Pattern;
import app.controller.impl.ProgressController;

import java.util.List;


public interface Reduce {
    void setBenchmarks(List<Pattern> benchmarks);
    void setnParam(int sizeOfNewParamList);
    void setDistortionRate(int distortionRate);
    List<Integer> reduce ();
    List<Integer> getReduceResults();
    void setProgressController(ProgressController progressController);
    void setVisualizationEnabled(boolean isVisualizationEnabled);
}
