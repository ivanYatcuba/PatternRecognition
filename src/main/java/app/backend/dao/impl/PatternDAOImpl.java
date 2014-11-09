package app.backend.dao.impl;
import app.backend.dao.PatternDAO;
import app.backend.model.*;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PatternDAOImpl implements PatternDAO {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(Pattern p) {
        sessionFactory.getCurrentSession().save(p);
    }

    @Override
    public List<Pattern> getBenchmarks() {
        return sessionFactory.getCurrentSession().createCriteria(Pattern.class).add(Restrictions.eq("isBenchmark", true)).list();
    }

    @Override
    public List<Pattern> getAll() {
        return sessionFactory.getCurrentSession().createCriteria(Pattern.class).add(
          Restrictions.eq("isBenchmark", false)).list();
    }

    @Override
    public List<Pattern> getChildrenPatterns(long id) {
        return sessionFactory.getCurrentSession().createCriteria(Pattern.class).add(Restrictions.eq("parentId", id)).list();
    }

    @Override
    public void removePattern(final Pattern p) {
        sessionFactory.getCurrentSession().delete(p);
    }

    @Override
    public Pattern findById(final long id) {
        return (Pattern)sessionFactory.getCurrentSession().get(Pattern.class, id);
    }


}
