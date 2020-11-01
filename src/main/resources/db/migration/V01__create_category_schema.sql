CREATE TABLE category (
    id IDENTITY NOT NULL PRIMARY KEY,
    name VARCHAR NOT NULL,
    parent_id BIGINT,
    FOREIGN KEY (parent_id) REFERENCES category(id)
);
