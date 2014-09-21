
CREATE TABLE bookmarks
(
    id serial PRIMARY KEY,
    users_id INT REFERENCES users(id),
    datestamp DATE NOT NULL DEFAULT CURRENT_DATE,
    url TEXT NOT NULL,
    tags TEXT NOT NULL,
    pagerank INT NOT NULL DEFAULT 0
);


