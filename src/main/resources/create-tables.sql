CREATE TABLE devices (
    id SERIAL PRIMARY KEY,
    sn INT NOT NULL,
    location VARCHAR (128)
);

CREATE TABLE faults (
  id   SERIAL PRIMARY KEY,
  type VARCHAR(256),
  occurrence_time TIMESTAMP NOT NULL
);

CREATE TABLE detections (
  id SERIAL PRIMARY KEY,
  device_id INT NOT NULL,
  fault_id INT NOT NULL,
  detection_time TIMESTAMP NOT NULL,
  CONSTRAINT detections_devices_id_fk FOREIGN KEY (device_id) REFERENCES devices (id) ON DELETE CASCADE,
  CONSTRAINT detections_faults_id_fk FOREIGN KEY (fault_id) REFERENCES faults (id) ON DELETE CASCADE
);

CREATE VIEW detection_view AS
  SELECT d2.sn, d2.location, f.type, f.occurrence_time, d.detection_time
  FROM detections d LEFT JOIN devices d2 ON d.device_id = d2.id LEFT JOIN faults f ON d.fault_id = f.id;
