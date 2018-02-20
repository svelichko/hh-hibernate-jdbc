package ru.hh.hibernatejdbc.detections;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import ru.hh.hibernatejdbc.devices.Device;
import ru.hh.hibernatejdbc.devices.DeviceDAO;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class DetectionService {

    private final SessionFactory sessionFactory;
    private final DetectionDAO detectionDAO;
    private final DeviceDAO deviceDAO;

    public DetectionService(SessionFactory sessionFactory, DetectionDAO detectionDAO, DeviceDAO deviceDAO) {
        this.sessionFactory = requireNonNull(sessionFactory);
        this.detectionDAO = requireNonNull(detectionDAO);
        this.deviceDAO = requireNonNull(deviceDAO);
    }

    public void register(int faultId) {
        inTransaction(() -> {
            Set<Device> deviceSet = deviceDAO.getRandom();
            for (Device device : deviceSet) {
                Detection detection = new Detection(device.getId(), faultId);
                detectionDAO.save(detection);
            }
        });
    }

    public void save(Detection detection) {
        inTransaction(() -> detectionDAO.save(detection));
    }

    public Optional<Detection> get(int detectionId) {
        return inTransaction(() -> detectionDAO.get(detectionId));
    }

    public Set<Detection> getAll() {
        return inTransaction(detectionDAO::getAll);
    }

    public void update(Detection detection) {
        inTransaction(() -> detectionDAO.update(detection));
    }

    public void delete(int detectionId) {
        inTransaction(() -> detectionDAO.delete(detectionId));
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
