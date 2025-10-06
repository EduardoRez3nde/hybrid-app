-- Habilita a extensão para gerar UUIDs
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Habilita a extensão para tipos de dados geográficos
CREATE EXTENSION IF NOT EXISTS "postgis";

-- Cria um tipo ENUM para os status da corrida, baseado na entidade Ride
CREATE TYPE ride_status AS ENUM (
    'REQUESTED',
    'ACCEPTED',
    'REJECTED',
    'IN_PROGRESS',
    'COMPLETED',
    'CANCELLED'
);