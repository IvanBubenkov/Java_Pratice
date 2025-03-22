DROP TABLE IF EXISTS Work_history CASCADE;
CREATE TABLE Work_history (
    record_id SERIAL PRIMARY KEY,
    vacancy_name VARCHAR(255) NOT NULL,
    applicant_id INT NOT NULL,
    company_id INT NOT NULL,
    salary INT NOT NULL,
    date_start DATE NOT NULL,
    date_end DATE,
    FOREIGN KEY (applicant_id) REFERENCES Site_user(user_id),
    FOREIGN KEY (company_id) REFERENCES Site_user(user_id)
);