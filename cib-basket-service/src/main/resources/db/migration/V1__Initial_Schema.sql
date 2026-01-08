CREATE TABLE basket (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    source_system VARCHAR(100) NOT NULL,
    owner VARCHAR(100),
    version BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE constituent (
    id UUID PRIMARY KEY,
    basket_id UUID NOT NULL,
    instrument_id VARCHAR(100) NOT NULL,
    instrument_type VARCHAR(50) NOT NULL,
    weight NUMERIC(18, 6) NOT NULL,
    quantity NUMERIC(18, 6),
    currency VARCHAR(10),
    divisor NUMERIC(18, 6),
    as_of TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_basket FOREIGN KEY (basket_id) REFERENCES basket(id) ON DELETE CASCADE
);

CREATE INDEX idx_constituent_basket_id ON constituent(basket_id);
CREATE INDEX idx_basket_status ON basket(status);
