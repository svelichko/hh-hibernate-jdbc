package ru.hh.hibernatejdbc.devices;

import java.util.Objects;

public class Device {

    private Integer id;
    private int sn;
    private String location;

    // factory method to create new device
    // can be constructor, but factory method has name that helps to understand its purpose
    public static Device create(int sn, String location) {
        return new Device(null, sn, location);
    }

    // factory method to load device from db
    // only DeviceDAO in the same package should use it, that is why it case package private visibility
    // id parameter is int - not Integer - existing entity should always have id
    static Device existing(int id, int sn, String location) {
        return new Device(id, sn, location);
    }

    // private constructor, only factory methods can use it
    private Device(Integer id, int sn, String location) {
        this.id = id;
        this.sn = sn;
        this.location = location;
    }

    public Integer getId() {
        return id;
    }

    // setter is package private - not public - to prevent changing id from outside
    // also id parameter is int, not Integer to prevent setting null
    void setId(int id) {
        this.id = id;
    }

    public int getSN() {
        return sn;
    }

    public void setSN(int sn) {
        this.sn = sn;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        Device thatDevice = (Device) that;
        return Objects.equals(id, thatDevice.id)
                && sn == thatDevice.sn
                && Objects.equals(location, thatDevice.location);
    }

    @Override
    public int hashCode() {
        // all new devices will have the same hashCode, which might lead to poor Map and Set performance
        // on the other side this hashCode implementation is super fast
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return String.format(
                "%s{id=%d, sn='%d', location='%s'}",
                getClass().getSimpleName(), id, sn, location
        );
    }
}
