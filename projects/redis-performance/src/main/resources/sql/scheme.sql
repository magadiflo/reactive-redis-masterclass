DROP TABLE IF EXISTS products;

CREATE TABLE products(
    id INTEGER GENERATED ALWAYS AS IDENTITY,
    description VARCHAR(500),
    price NUMERIC(10,2),
    CONSTRAINT pk_products PRIMARY KEY(id)
);
