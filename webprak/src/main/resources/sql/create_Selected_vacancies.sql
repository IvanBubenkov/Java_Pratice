DROP TABLE IF EXISTS Selected_vacancies CASCADE;
CREATE TABLE Selected_vacancies (
    record_id SERIAL PRIMARY KEY,
    vacancy_id INT NOT NULL,
    applicant_id INT NOT NULL,
    FOREIGN KEY (vacancy_id) REFERENCES Vacancy(vacancy_id),
    FOREIGN KEY (applicant_id) REFERENCES Site_user(user_id)
);