CREATE TABLE employee (
                          id BIGSERIAL PRIMARY KEY,
                          first_name VARCHAR(255),
                          email VARCHAR(255) UNIQUE,
                          password VARCHAR(255),
                          role VARCHAR(50)
);

CREATE TABLE kennel (
                        id BIGSERIAL PRIMARY KEY,
                        code VARCHAR(255),
                        capacity INTEGER,
                        type VARCHAR(50)
);

CREATE TABLE animal (
                        id BIGSERIAL PRIMARY KEY,
                        species VARCHAR(255),
                        name VARCHAR(255),
                        birth_date DATE,
                        adoption_status VARCHAR(50),
                        is_quarantined BOOLEAN,
                        kennel_id BIGINT REFERENCES kennel(id)
);