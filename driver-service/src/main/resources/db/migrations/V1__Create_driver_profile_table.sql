-- Ativa a extensão PostGIS se ela ainda não existir
CREATE EXTENSION IF NOT EXISTS postgis;

-- Cria a tabela de perfis de motoristas
CREATE TABLE tb_driver_profile (
    user_id UUID NOT NULL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    cnh_number VARCHAR(255) UNIQUE,
    current_location GEOGRAPHY(POINT, 4326),
    approval_status VARCHAR(255),
    operational_status VARCHAR(255),
    has_approved_vehicle BOOLEAN NOT NULL,
    average_rating DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    total_ratings BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE
);