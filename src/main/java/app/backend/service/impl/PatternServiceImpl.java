package app.backend.service.impl;

import app.backend.dao.PatternDAO;
import app.backend.service.PatternService;
import app.backend.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PatternServiceImpl implements PatternService {

    @Autowired
    private PatternDAO patternDAO;

    @Override
    @Transactional
    public void save(Pattern p) {
        patternDAO.save(p);
    }

    @Override
    @Transactional
    public List<Pattern> getAll() {
        return patternDAO.getAll();
    }

    @Override
    @Transactional
    public List<Pattern> getBenchmarks() {
        return patternDAO.getBenchmarks();
    }

    @Override
    @Transactional
    public List<Pattern> getChildrenPatterns(long id) {
        return patternDAO.getChildrenPatterns(id);
    }

    @Override
    @Transactional
    public Pattern findById(final long id) {
        return patternDAO.findById(id);
    }

    @Override
    @Transactional
    public void removeDistorted(long id) {
        for(Pattern p : patternDAO.getChildrenPatterns(id)){
            patternDAO.removePattern(p);
        }
    }
}
