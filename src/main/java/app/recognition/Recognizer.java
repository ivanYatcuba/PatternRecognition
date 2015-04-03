package app.recognition;

import app.backend.model.Pattern;
import java.util.List;

public interface Recognizer {
    Pattern recognize(Pattern input);
    void setTrainSet(List<Pattern> trainSet);
    List<Pattern> getTrainSet();
    void init();
    void setAttributesToIgnore(List<Integer> indexes);
}
