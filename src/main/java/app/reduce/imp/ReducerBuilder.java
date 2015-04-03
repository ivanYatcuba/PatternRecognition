package app.reduce.imp;

import app.backend.model.Pattern;
import app.controller.impl.ProgressController;
import app.reduce.Reduce;

import java.util.List;

public class ReducerBuilder {

    Reduce reduce;

    public ReducerBuilder(Class<? extends Reduce> aClass) {
        try {
            reduce = aClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException("Not supported type!");
        }
    }

    public ReducerBuilder setBenchmarks(List<Pattern> benchmarks) {
        reduce.setBenchmarks(benchmarks);
        return this;
    }

    public ReducerBuilder setSizeOfNewParamList(int sizeOfNewParamList) {
        reduce.setnParam(sizeOfNewParamList);
        return this;
    }

    public ReducerBuilder setDistortionRate(int distortionRate) {
        reduce.setDistortionRate(distortionRate);
        return this;
    }

    public ReducerBuilder setProgressController(ProgressController controller) {
        reduce.setProgressController(controller);
        return this;
    }

    public ReducerBuilder setVisulaization(boolean visualization) {
        reduce.setVisualizationEnabled(visualization);
        return this;
    }


    public Reduce build() {
        return reduce;
    }
}
