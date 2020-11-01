CREATE TABLE product (
    id IDENTITY NOT NULL PRIMARY KEY,
    name VARCHAR NOT NULL,
    price NUMERIC(7, 2) NOT NULL,
    currency VARCHAR NOT NULL,
    category_id BIGINT NOT NULL,
    FOREIGN KEY (category_id) REFERENCES category(id)
);
