package ru.hh.hibernatejdbc.faults;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public class FaultDAO {

    private final SessionFactory sessionFactory;

    public FaultDAO(SessionFactory sessionFactory) {
        this.sessionFactory = requireNonNull(sessionFactory);
    }

    public void save(Fault fault) {
        if (fault.id() != null) {
            throw new IllegalArgumentException("can not save " + fault + " with assigned id");
        }
        session().save(fault);
    }

    public Optional<Fault> get(int faultId) {
        Fault fault = (Fault)session().get(Fault.class, faultId);
        return Optional.ofNullable(fault);
    }

    public Set<Fault> getAll() {
        Criteria criteria = session().createCriteria(Fault.class);

        List<Fault> faults = criteria.list();
        return new HashSet<>(faults);
    }

    public void update(Fault fault) {
        session().update(fault);
    }

    public void delete(int faultId) {
        session().createQuery("DELETE Fault WHERE id = :id")
                .setInteger("id", faultId)
                .executeUpdate();
    }

    private Session session() {
        return sessionFactory.getCurrentSession();
    }
}
