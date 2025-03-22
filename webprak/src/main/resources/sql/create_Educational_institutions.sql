DROP TABLE IF EXISTS Educational_institutions CASCADE;
CREATE TABLE Educational_institutions (
    education_id SERIAL PRIMARY KEY,
    education_level VARCHAR(255) NOT NULL
);