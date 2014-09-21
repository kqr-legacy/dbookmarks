
CREATE TABLE users
(
    id serial PRIMARY KEY,
    username VARCHAR(60) UNIQUE NOT NULL,
    password VARCHAR(60) NOT NULL
);

