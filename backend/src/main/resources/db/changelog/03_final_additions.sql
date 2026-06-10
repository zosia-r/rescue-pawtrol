-- liquibase formatted sql
-- changeset projekt:3

ALTER TABLE intervention ADD COLUMN description VARCHAR(500);

CREATE TABLE medical_record (
    id BIGSERIAL PRIMARY KEY,
    record_date DATE,
    record_type VARCHAR(50),
    description TEXT,
    doctor VARCHAR(255),
    animal_id BIGINT REFERENCES animal(id)
);

CREATE TABLE adoption_card (
   id BIGSERIAL PRIMARY KEY,
   status VARCHAR(50),
   candidate_details VARCHAR(255),
   submission_date DATE,
   finalization_date DATE,
   animal_id BIGINT UNIQUE REFERENCES animal(id)
);