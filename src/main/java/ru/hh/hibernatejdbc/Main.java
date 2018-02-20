package ru.hh.hibernatejdbc;

import org.hibernate.SessionFactory;
import ru.hh.hibernatejdbc.detections.Detection;
import ru.hh.hibernatejdbc.detections.DetectionDAO;
import ru.hh.hibernatejdbc.detections.DetectionService;
import ru.hh.hibernatejdbc.devices.Device;
import ru.hh.hibernatejdbc.devices.DeviceDAO;
import ru.hh.hibernatejdbc.faults.Fault;
import ru.hh.hibernatejdbc.faults.FaultDAO;
import ru.hh.hibernatejdbc.faults.FaultService;
import ru.hh.hibernatejdbc.utils.HibernateConfigFactory;
import ru.hh.hibernatejdbc.utils.PropertiesFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import static ru.hh.hibernatejdbc.utils.DataSourceFactory.createPGSimpleDataSource;

class Main {

    public static void main(final String... args) throws IOException {

        final Properties properties = PropertiesFactory.load();

        final DataSource dataSource = createPGSimpleDataSource(
                properties.getProperty("jdbc.url"),
                properties.getProperty("jdbc.user"),
                properties.getProperty("jdbc.password")
        );

        final DeviceDAO deviceDAO = new DeviceDAO(dataSource);

        System.out.println("PLAY WITH DEVICES USING JDBC");
        System.out.println();
        play(deviceDAO);

        System.out.println("FILL DB WITH DEVICES");
        System.out.println();
        fill(deviceDAO, 10);

        SessionFactory sessionFactory = createSessionFactory();
        try {

            DetectionService detectionService = createDetectionService(sessionFactory, dataSource);
            FaultService faultService = createFaultService(sessionFactory, detectionService);

            System.out.println("FILL DB WITH FAULTS");
            System.out.println();

            Fault shortCircuit1 = new Fault("short circuit (L1, L2)");
            System.out.println("persisting " + shortCircuit1);
            faultService.save(shortCircuit1);

            Fault earthFault1 = new Fault("earth fault (L2)");
            System.out.println("persisting " + earthFault1);
            faultService.save(earthFault1);

            Fault shortCircuit2 = new Fault("short circuit (L2, L3)");
            System.out.println("persisting " + shortCircuit2);
            faultService.save(shortCircuit2);

            System.out.println("faults in db: " + faultService.getAll());
            System.out.println();

            System.out.println("REPORT ON DETECTED FAULTS");
            System.out.println();

            System.out.println("detections in db: " + detectionService.getAll());
            System.out.println();

            reportDetections(new DeviceDAO(dataSource), detectionService, faultService);

        } finally {
            sessionFactory.close();
        }
    }

    private static void play(final DeviceDAO deviceDAO) {

        final Device device = Device.create(18123456, "Line 16 pillar 2");
        deviceDAO.insert(device);
        System.out.println("persisted " + device);
        System.out.println("devices in db: " + deviceDAO.getAll());
        System.out.println();

        device.setSN(18654321);
        deviceDAO.update(device);
        System.out.println("updated SN 123456 to 654321");
        System.out.println("devices in db: " + deviceDAO.getAll());
        System.out.println();

        deviceDAO.delete(device.getId());
        System.out.println("deleted device with id " + device.getId());
        System.out.println("devices in db: " + deviceDAO.getAll());
        System.out.println();

        final Optional<Device> emptyNewDevice = deviceDAO.get(device.getId());
        System.out.println("tried to get device by " + device.getId() + " but got " + emptyNewDevice);
        System.out.println();
    }

    private static void fill(final DeviceDAO deviceDAO, int devCount) {

        Random random = new Random();
        int sn, branchLine, pillar;

        for (int i = 0; i < devCount; i++) {
            sn = 100000 + random.nextInt(1000000 - 100000);
            branchLine = 1 + random.nextInt(100);
            pillar = 1 + random.nextInt(1000);

            final Device device = Device.create(sn,
                    "branch line " + branchLine + " pillar " + pillar);
            deviceDAO.insert(device);
            System.out.println("persisted " + device);
        }

        System.out.println("devices in db: " + deviceDAO.getAll());
    }

    private static void reportDetections(final DeviceDAO deviceDAO,
                                         final DetectionService detectionService,
                                         final FaultService faultService) {

        Set<Detection> detectionSet = detectionService.getAll();

        for (Detection detection : detectionSet) {
            Optional<Device> deviceOptional = deviceDAO.get(detection.deviceId());
            Optional<Fault> faultOptional = faultService.get(detection.faultId());

            if (deviceOptional.isPresent() && faultOptional.isPresent()) {
                System.out.println("device with sn " + deviceOptional.get().getSN() +
                        ", located at " + deviceOptional.get().getLocation() +
                        ", has detected " + faultOptional.get().type() + " at " + detection.detectionTime() +
                        ", which occurred at " + faultOptional.get().occurrenceTime());
            }
        }
    }

    private static SessionFactory createSessionFactory() {
        return HibernateConfigFactory.prod().buildSessionFactory();
    }

    private static DetectionService createDetectionService(final SessionFactory sessionFactory, final DataSource dataSource) {
        DetectionDAO detectionDAO = new DetectionDAO(sessionFactory);
        DeviceDAO deviceDAO = new DeviceDAO(dataSource);
        return new DetectionService(sessionFactory, detectionDAO, deviceDAO);
    }

    private static FaultService createFaultService(final SessionFactory sessionFactory, final DetectionService detectionService) {
        FaultDAO faultDAO = new FaultDAO(sessionFactory);
        return new FaultService(sessionFactory, faultDAO, detectionService);
    }

    private Main() {
    }
}
