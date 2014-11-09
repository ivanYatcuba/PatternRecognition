package app.recognition;

import app.backend.model.Pattern;

import java.util.List;

public interface Recognizer {
    Pattern recognize(Pattern input);
    void setTrainSet(List<Pattern> trainSet);
    void init();
}
