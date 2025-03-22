DROP TABLE IF EXISTS Site_user CASCADE;
CREATE TABLE Site_user (
    user_id SERIAL PRIMARY KEY,
    login VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role INT NOT NULL,
    Status INT NOT NULL,
    full_name_company VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    home_address VARCHAR(255),
    education INT DEFAULT NULL,
    FOREIGN KEY (role) REFERENCES Roles(role_id),
    FOREIGN KEY (Status) REFERENCES User_statuses(status_id),
    FOREIGN KEY (education) REFERENCES Educational_institutions(education_id)
);