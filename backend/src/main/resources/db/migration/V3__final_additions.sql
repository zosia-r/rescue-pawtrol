-- 1. Dodanie pola description do interwencji (z poprzedniego kroku)
ALTER TABLE intervention ADD COLUMN description VARCHAR(500);

-- 2. Tabela dokumentacji medycznej
CREATE TABLE medical_record (
                                id BIGSERIAL PRIMARY KEY,
                                record_date DATE,
                                record_type VARCHAR(50),
                                description TEXT,
                                doctor VARCHAR(255),
                                animal_id BIGINT REFERENCES animal(id)
);

-- 3. Nowa tabela: Karty Adopcyjne
CREATE TABLE adoption_card (
                               id BIGSERIAL PRIMARY KEY,
                               status VARCHAR(50),
                               candidate_details VARCHAR(255),
                               submission_date DATE,
                               finalization_date DATE,
                               animal_id BIGINT UNIQUE REFERENCES animal(id)
);