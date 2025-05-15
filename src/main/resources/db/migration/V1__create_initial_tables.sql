CREATE TABLE organizations (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE employees (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    dundie_awards INTEGER,
    organization_id BIGINT REFERENCES organizations(id) ON DELETE CASCADE
);

CREATE TABLE activities (
    id BIGSERIAL PRIMARY KEY,
    occured_at TIMESTAMP NOT NULL,
    event TEXT NOT NULL
);

CREATE TABLE dundie_delivery (
    id BIGSERIAL PRIMARY KEY,
    identifier UUID NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    finished_at TIMESTAMP,
    organization_id BIGINT REFERENCES organizations(id) ON DELETE CASCADE,
    status VARCHAR(50) NOT NULL
);

CREATE TABLE dundie_delivery_status (
    id BIGSERIAL PRIMARY KEY,
    dundie_delivery_id BIGINT REFERENCES dundie_delivery(id) ON DELETE CASCADE,
    created_at TIMESTAMP,
    status VARCHAR(50) NOT NULL
);

CREATE TABLE dundie_delivery_chunk (
    id BIGSERIAL PRIMARY KEY,
    dundie_delivery_id BIGINT REFERENCES dundie_delivery(id) ON DELETE CASCADE,
    start_employee_id BIGINT,
    end_employee_id BIGINT,
    created_at TIMESTAMP,
    finished_at TIMESTAMP,
    status VARCHAR(50) NOT NULL
);