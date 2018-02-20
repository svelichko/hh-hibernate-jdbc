package ru.hh.hibernatejdbc.devices;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class DeviceDAO {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert deviceJdbcInsert;

    public DeviceDAO(DataSource dataSource) {

        jdbcTemplate = new JdbcTemplate(dataSource);
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        deviceJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("devices")
                .usingGeneratedKeyColumns("id");
    }

    public void insert(Device device) {

        if (device.getId() != null) {
            throw new IllegalArgumentException("can not insert " + device + " with already assigned id");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("sn", device.getSN());
        params.put("location", device.getLocation());

        int deviceId = deviceJdbcInsert.executeAndReturnKey(params).intValue();

        device.setId(deviceId);
    }

    public Optional<Device> get(int deviceId) {

        String query = "SELECT id, sn, location FROM devices WHERE id = :device_id";

        Map<String, Object> params = new HashMap<>();
        params.put("device_id", deviceId);

        Device device;
        try {
            device = namedParameterJdbcTemplate.queryForObject(query, params, rowToDevice);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
        return Optional.of(device);
    }

    public Set<Device> getAll() {

        String query = "SELECT id, sn, location FROM devices";

        return new HashSet<>(jdbcTemplate.query(query, rowToDevice));
    }

    public Set<Device> getRandom() {

        String query = "SELECT id, sn, location FROM devices ORDER BY random() " +
                "LIMIT (SELECT floor(1 + random() * (SELECT count(id) FROM devices)))";

        return new HashSet<>(jdbcTemplate.query(query, rowToDevice));
    }

    public void update(Device device) {

        if (device.getId() == null) {
            throw new IllegalArgumentException("can not update " + device + " without id");
        }

        String query = "UPDATE devices SET sn = :sn, location = :location WHERE id = :device_id";

        Map<String, Object> params = new HashMap<>();
        params.put("sn", device.getSN());
        params.put("location", device.getLocation());
        params.put("device_id", device.getId());

        namedParameterJdbcTemplate.update(query, params);
    }

    public void delete(int deviceId) {

        String query = "DELETE FROM devices WHERE id = :device_id";

        Map<String, Object> params = new HashMap<>();
        params.put("device_id", deviceId);

        namedParameterJdbcTemplate.update(query, params);
    }

    private static final RowMapper<Device> rowToDevice = (resultSet, rowNum) ->
            Device.existing(
                    resultSet.getInt("id"),
                    resultSet.getInt("sn"),
                    resultSet.getString("location")
            );
}
