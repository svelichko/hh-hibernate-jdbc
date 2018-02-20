package ru.hh.hibernatejdbc.faults;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "faults")
public class Fault {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "type")
    private String type;

    @Column(name = "occurrence_time", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date occurrenceTime;

    public Fault(String type) {
        this.type = type;
        this.occurrenceTime = new Date();
    }

    /** for Hibernate **/
    Fault() {}

    public Integer id() {
        return id;
    }

    public String type() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date occurrenceTime() {
        return occurrenceTime;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that)
            return true;
        if (that == null || getClass() != that.getClass())
            return false;

        Fault thatFault = (Fault)that;
        return Objects.equals(id, thatFault.id)
                && occurrenceTime.equals(thatFault.occurrenceTime)
                && Objects.equals(type, thatFault.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, occurrenceTime);
    }

    @Override
    public String toString() {
        return String.format("%s{id=%d, type='%s', occurrenceTime='%s'}",
                getClass().getSimpleName(), id, type, occurrenceTime);
    }
}
