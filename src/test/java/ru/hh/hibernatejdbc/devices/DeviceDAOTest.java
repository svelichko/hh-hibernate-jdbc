package ru.hh.hibernatejdbc.devices;

import org.junit.Test;
import ru.hh.hibernatejdbc.JDBCTestBase;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DeviceDAOTest extends JDBCTestBase {

    private static final DeviceDAO deviceDao = new DeviceDAO(database);

    protected DeviceDAO deviceDAO() {
        return deviceDao;
    }

    @Test
    public void insertShouldInsertNewDeviceInDBAndReturnDeviceWithAssignedId() throws Exception {

        Device device1 = Device.create(147258, "branch line 3 pillar 20");
        Device device2 = Device.create(528963, "branch line 10 pillar 3");

        deviceDAO().insert(device1);
        deviceDAO().insert(device2);

        Device device1FromDB = deviceDAO().get(device1.getId()).get();
        assertEquals(device1, device1FromDB);

        Device device2FromDB = deviceDAO().get(device2.getId()).get();
        assertEquals(device2, device2FromDB);
    }

    @Test(expected = IllegalArgumentException.class)
    public void insertShouldThrowIllegalArgumentExceptionIfDeviceHasId() throws Exception {

        Device device = Device.existing(1, 0, "location");

        deviceDAO().insert(device);
    }

    @Test
    public void getShouldReturnDevice() throws Exception {

        Device device = Device.create(147258, "branch line 3 pillar 20");
        deviceDAO().insert(device);

        Optional<Device> deviceFromDB = deviceDAO().get(device.getId());

        assertEquals(device, deviceFromDB.get());
    }

    @Test
    public void getShouldReturnEmptyOptionalIfNoDeviceWithSuchId() throws Exception {

        int nonExistentDeviceId = 666;

        Optional<Device> deviceFromDB = deviceDAO().get(nonExistentDeviceId);

        assertFalse(deviceFromDB.isPresent());
    }

    @Test
    public void getAllShouldReturnAllDevices() throws Exception {

        assertTrue(deviceDAO().getAll().isEmpty());

        Device device1 = Device.create(963852, "branch line 12 pilalr 7");
        Device device2 = Device.create(456123, "branch line 4 pillar 1");

        deviceDAO().insert(device1);
        deviceDAO().insert(device2);

        Set<Device> devicesFromDB = deviceDAO().getAll();

        assertEquals(new HashSet<>(Arrays.asList(device1, device2)), devicesFromDB);
    }

    @Test
    public void updateShouldUpdateDevice() throws Exception {

        Device device = Device.create(963852, "branch line 12 pillar 7");
        deviceDAO().insert(device);
        device.setSN(147258);

        deviceDAO().update(device);

        Device deviceFromDB = deviceDAO().get(device.getId()).get();
        assertEquals(device, deviceFromDB);
    }

    @Test
    public void deleteShouldDeleteDeviceById() throws Exception {

        Device device1 = Device.create(963852, "branch line 12 pilalr 7");
        Device device2 = Device.create(456123, "branch line 4 pillar 1");

        deviceDAO().insert(device1);
        deviceDAO().insert(device2);

        deviceDAO().delete(device1.getId());

        assertFalse(deviceDAO().get(device1.getId()).isPresent());
        assertTrue(deviceDAO().get(device2.getId()).isPresent());
    }
}
