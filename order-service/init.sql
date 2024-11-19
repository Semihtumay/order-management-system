-- uuid-ossp eklentisini yükleyin
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


-- Order Service için init.sql
CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    total_amount NUMERIC(10, 3) NOT NULL,
    order_date TIMESTAMP NOT NULL
    );

CREATE TABLE IF NOT EXISTS order_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID REFERENCES orders(id) ON DELETE CASCADE,
    product_id VARCHAR(255) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    price NUMERIC(10, 3) NOT NULL,
    tax NUMERIC(5, 2) NOT NULL,
    discount NUMERIC(5, 2) NOT NULL,
    quantity INT NOT NULL,
    total_price NUMERIC(10, 3) NOT NULL
    );

CREATE TABLE outbox (
                        id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
                        aggregatetype VARCHAR(255),
                        aggregateid VARCHAR(255),
                        type VARCHAR(255),
                        payload varchar(2000),
                        created_at TIMESTAMP
);