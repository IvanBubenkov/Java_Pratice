DROP TABLE IF EXISTS User_statuses CASCADE;
CREATE TABLE User_statuses (
    status_id SERIAL PRIMARY KEY,
    status_name VARCHAR(255) NOT NULL
);