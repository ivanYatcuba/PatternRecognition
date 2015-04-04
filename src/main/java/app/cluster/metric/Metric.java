package app.cluster.metric;

import app.backend.model.Pattern;

public interface Metric {
   double calculate(Pattern p1, Pattern p2);
}
