DROP TABLE IF EXISTS Selected_resumes CASCADE;
CREATE TABLE Selected_resumes (
    record_id SERIAL PRIMARY KEY,
    resume_id INT NOT NULL,
    company_id INT NOT NULL,
    FOREIGN KEY (resume_id) REFERENCES Resume(resume_id) ON DELETE CASCADE,
    FOREIGN KEY (company_id) REFERENCES Site_user(user_id) ON DELETE CASCADE
);