package app.reduce;

import app.backend.model.Pattern;

import java.util.List;

public interface Reduce {
    void setBenchmarks(List<Pattern> benchmarks);
    void setSizeOfNewParamList(int sizeOfNewParamList);
    void setDistortionRate(int distortionRate);
    List<Pattern> reduce ();
}
