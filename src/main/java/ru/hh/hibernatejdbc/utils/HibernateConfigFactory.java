package ru.hh.hibernatejdbc.utils;

import org.hibernate.cfg.Configuration;
import ru.hh.hibernatejdbc.detections.Detection;
import ru.hh.hibernatejdbc.faults.Fault;

public class HibernateConfigFactory {

    public static Configuration prod() {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(Fault.class);
        configuration.addAnnotatedClass(Detection.class);
        return configuration;
    }

    private HibernateConfigFactory() {
    }
}
