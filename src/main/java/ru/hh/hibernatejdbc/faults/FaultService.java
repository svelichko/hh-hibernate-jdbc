package ru.hh.hibernatejdbc.faults;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import ru.hh.hibernatejdbc.detections.DetectionService;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class FaultService {

    private final SessionFactory sessionFactory;
    private final FaultDAO faultDAO;
    private final DetectionService detectionService;

    public FaultService(SessionFactory sessionFactory, FaultDAO faultDAO, DetectionService detectionService) {
        this.sessionFactory = requireNonNull(sessionFactory);
        this.faultDAO = requireNonNull(faultDAO);
        this.detectionService = requireNonNull(detectionService);
    }

    public void save(Fault fault) {
        inTransaction(() -> {
            faultDAO.save(fault);
            detectionService.register(fault.id());
        });
    }

    public Optional<Fault> get(int faultId) {
        return inTransaction(() -> faultDAO.get(faultId));
    }

    public Set<Fault> getAll() {
        return inTransaction(faultDAO::getAll);
    }

    public void update(Fault fault) {
        inTransaction(() -> faultDAO.update(fault));
    }

    public void delete(int faultId) {
        inTransaction(() -> faultDAO.delete(faultId));
    }

    private <T> T inTransaction(Supplier<T> supplier) {
        Optional<Transaction> transaction = beginTransaction();
        try {
            T result = supplier.get();
            transaction.ifPresent(Transaction::commit);
            return result;
        } catch (RuntimeException e) {
            transaction.ifPresent(Transaction::rollback);
            throw e;
        }
    }

    private void inTransaction(Runnable runnable) {
        inTransaction(() -> {
            runnable.run();
            return null;
        });
    }

    private Optional<Transaction> beginTransaction() {
        Transaction transaction = sessionFactory.getCurrentSession().getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
            return Optional.of(transaction);
        }
        return Optional.empty();
    }
}
