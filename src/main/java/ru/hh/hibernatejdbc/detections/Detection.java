package ru.hh.hibernatejdbc.detections;

import ru.hh.hibernatejdbc.faults.Fault;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "detections")
public class Detection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "device_id")
    private int deviceId;

    @Column(name = "fault_id")
    private int faultId;

    @Column(name = "detection_time", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date detectionTime;

    public Detection(int deviceId, int faultId) {
        this.deviceId = deviceId;
        this.faultId = faultId;
        this.detectionTime = new Date();
    }

    /** for Hibernate **/
    Detection() {}

    public Integer id() {
        return id;
    }

    public int deviceId() {
        return deviceId;
    }

    public int faultId() {
        return faultId;
    }

    public Date detectionTime() {
        return detectionTime;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that)
            return true;
        if (that == null || getClass() != that.getClass())
            return false;

        Detection thatDetection = (Detection)that;
        return Objects.equals(id, thatDetection.id)
                && detectionTime.equals(thatDetection.detectionTime)
                && deviceId == thatDetection.deviceId
                && faultId == thatDetection.faultId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, detectionTime);
    }

    @Override
    public String toString() {
        return String.format("%s{id=%d, device_id='%d', fault_id='%d', detectionTime='%s'}",
                getClass().getSimpleName(), id, deviceId, faultId, detectionTime);
    }
}
