CREATE TABLE intervention (
                              id BIGSERIAL PRIMARY KEY,
                              latitude DOUBLE PRECISION,
                              longitude DOUBLE PRECISION,
                              report_time TIMESTAMP,
                              status VARCHAR(50),
                              driver_id BIGINT REFERENCES employee(id)
);

CREATE TABLE route_point (
                             id BIGSERIAL PRIMARY KEY,
                             step_order INTEGER,
                             address VARCHAR(255),
                             latitude DOUBLE PRECISION,
                             longitude DOUBLE PRECISION,
                             point_type VARCHAR(50),
                             intervention_id BIGINT REFERENCES intervention(id)
);

CREATE TABLE adoption_status_history (
                                         id BIGSERIAL PRIMARY KEY,
                                         date DATE,
                                         status VARCHAR(50),
                                         owner VARCHAR(255),
                                         notes TEXT,
                                         animal_id BIGINT REFERENCES animal(id)
);