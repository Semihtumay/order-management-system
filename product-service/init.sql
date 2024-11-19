-- uuid-ossp eklentisini yükleyin
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


-- Product Service için init.sql
CREATE TABLE IF NOT EXISTS products (
                                        id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                        name VARCHAR(255) NOT NULL,
                                        price NUMERIC(10, 3) NOT NULL CHECK (price >= 0),
                                        quantity INT NOT NULL CHECK (quantity >= 0),
                                        tax NUMERIC(5, 2) NOT NULL CHECK (tax >= 0 AND tax <= 1),
                                        discount NUMERIC(5, 2) NOT NULL CHECK (discount >= 0 AND discount <= 1),
                                        is_deleted BOOLEAN DEFAULT FALSE,
                                        total_price NUMERIC(10, 3) NOT NULL CHECK (total_price >= 0)
);

