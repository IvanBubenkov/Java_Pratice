DROP TABLE IF EXISTS Vacancy CASCADE;
CREATE TABLE Vacancy (
    vacancy_id SERIAL PRIMARY KEY,
    vacancy_name VARCHAR(255) NOT NULL,
    company_id INT NOT NULL,
    description TEXT,
    min_salary DECIMAL(10,2),
    education INT,
    number_of_views INT DEFAULT 0,
    FOREIGN KEY (company_id) REFERENCES Site_user(user_id),
    FOREIGN KEY (education) REFERENCES Educational_institutions(education_id)
);