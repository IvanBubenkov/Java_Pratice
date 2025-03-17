CREATE TABLE User_statuses (
    status_id SERIAL PRIMARY KEY,
    status_name VARCHAR(255) NOT NULL
);

CREATE TABLE Roles (
    role_id SERIAL PRIMARY KEY,
    role_name VARCHAR(255) NOT NULL
);

CREATE TABLE Educational_institutions (
    education_id SERIAL PRIMARY KEY,
    education_level VARCHAR(255) NOT NULL
);

CREATE TABLE Site_user (
    user_id SERIAL PRIMARY KEY,
    login VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role INT NOT NULL,
    Status INT NOT NULL,
    full_name_company VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    home_address VARCHAR(255),
    education INT,
    FOREIGN KEY (role) REFERENCES Roles(role_id),
    FOREIGN KEY (Status) REFERENCES User_statuses(status_id),
    FOREIGN KEY (education) REFERENCES Educational_institutions(education_id)
);

CREATE TABLE Vacancy (
    vacancy_id SERIAL PRIMARY KEY,
    vacancy_name VARCHAR(255) NOT NULL,
    company_id INT NOT NULL,
    description TEXT,
    min_salary DECIMAL(10,2),
    education INT,
    number_of_views INT DEFAULT 0,
    FOREIGN KEY (education) REFERENCES Educational_institutions(education_id)
);

CREATE TABLE Resume (
    resume_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    min_salary_req DECIMAL(10,2),
    number_of_views INT DEFAULT 0,
    vacancy_name VARCHAR(255) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Site_user(user_id)
);

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

CREATE TABLE Selected_resumes (
    record_id SERIAL PRIMARY KEY,
    resume_id INT NOT NULL,
    company_id INT NOT NULL,
    FOREIGN KEY (resume_id) REFERENCES Resume(resume_id),
    FOREIGN KEY (company_id) REFERENCES Site_user(user_id)
);

CREATE TABLE Selected_vacancies (
    record_id SERIAL PRIMARY KEY,
    vacancy_id INT NOT NULL,
    applicant_id INT NOT NULL,
    FOREIGN KEY (vacancy_id) REFERENCES Vacancy(vacancy_id),
    FOREIGN KEY (applicant_id) REFERENCES Site_user(user_id)
);
