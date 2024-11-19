-- uuid-ossp eklentisini yükleyin
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


-- Invoice Service için init.sql
CREATE TABLE IF NOT EXISTS invoices (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id VARCHAR(255) NOT NULL,
    total_amount NUMERIC(10, 3) NOT NULL,
    invoice_date TIMESTAMP NOT NULL
    );

CREATE TABLE IF NOT EXISTS invoice_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    invoice_id UUID REFERENCES invoices(id) ON DELETE CASCADE,
    product_name VARCHAR(255) NOT NULL,
    price NUMERIC(10, 3) NOT NULL,
    tax NUMERIC(5, 2) NOT NULL,
    discount NUMERIC(5, 2) NOT NULL,
    quantity INT NOT NULL,
    total_price NUMERIC(10, 3) NOT NULL
    );
