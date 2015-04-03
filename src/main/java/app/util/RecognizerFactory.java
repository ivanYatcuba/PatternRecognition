package app.util;

import app.backend.model.Pattern;
import app.recognition.impl.CFourFive;
import app.recognition.impl.KNN;
import app.recognition.impl.SolutionTreeBagging;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RecognizerFactory {

    public KNN getKNN(int k, List<Pattern> trainSet, List<Pattern> benchmarks) {
        return new KNN(k, trainSet, benchmarks);
    }

    public CFourFive getCFourFive(List<Pattern> benchmarks, List<Pattern> trainSet) {
        CFourFive cFourFive = new CFourFive(benchmarks, trainSet);
        cFourFive.init();
        return cFourFive;
    }

    public SolutionTreeBagging getSolutionTreeBagging(List<Pattern> benchmarks, List<Pattern> trainSet) {
        SolutionTreeBagging solutionTreeBagging = new SolutionTreeBagging(benchmarks, trainSet);
        solutionTreeBagging.init();
        return solutionTreeBagging;
    }
}
