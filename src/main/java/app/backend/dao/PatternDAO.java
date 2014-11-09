package app.backend.dao;

import org.springframework.stereotype.Repository;
import app.backend.model.*;

import java.util.List;

public interface PatternDAO {

    public void save(Pattern p);
    public List<Pattern> getBenchmarks();
    public List<Pattern> getAll();
    public List<Pattern> getChildrenPatterns(long id);
    public void removePattern(Pattern p);
    public Pattern findById(long id);
}
