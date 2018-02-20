package ru.hh.hibernatejdbc.detections;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public class DetectionDAO {

    private final SessionFactory sessionFactory;

    public DetectionDAO(SessionFactory sessionFactory) {
        this.sessionFactory = requireNonNull(sessionFactory);
    }

    public void save(Detection detection) {
        if (detection.id() != null) {
            throw new IllegalArgumentException("can not save " + detection + " with assigned id");
        }
        session().save(detection);
    }

    public Optional<Detection> get(int detectionId) {
        Detection detection = (Detection)session().get(Detection.class, detectionId);
        return Optional.ofNullable(detection);
    }

    public Set<Detection> getAll() {
        Criteria criteria = session().createCriteria(Detection.class);

        List<Detection> detections = criteria.list();
        return new HashSet<>(detections);
    }

    public void update(Detection detection) {
        session().update(detection);
    }

    public void delete(int detectionId) {
        session().createQuery("DELETE Detection WHERE id = :id")
                .setInteger("id", detectionId)
                .executeUpdate();
    }

    private Session session() {
        return sessionFactory.getCurrentSession();
    }
}
