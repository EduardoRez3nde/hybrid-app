-- Ativa extensão uuid-ossp (para gerar UUIDs no PostgreSQL, se necessário)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE tb_users (
    id UUID PRIMARY KEY NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role_type VARCHAR(255) NOT NULL,
    account_status VARCHAR(255) NOT NULL
);
