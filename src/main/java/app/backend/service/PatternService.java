package app.backend.service;

import app.backend.model.*;

import java.util.List;

public interface PatternService {

    public void save(Pattern p);
    public List<Pattern> getAll();
    public List<Pattern> getBenchmarks();
    public List<Pattern> getChildrenPatterns(long id);
    public Pattern findById(long id);
    public void removeDistorted(long id);
    int getDataSize();
}
