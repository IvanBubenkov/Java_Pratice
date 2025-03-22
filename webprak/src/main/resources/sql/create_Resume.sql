DROP TABLE IF EXISTS Resume CASCADE;
CREATE TABLE Resume (
    resume_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    min_salary_req DECIMAL(10,2),
    number_of_views INT DEFAULT 0,
    vacancy_name VARCHAR(255) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Site_user(user_id)
);