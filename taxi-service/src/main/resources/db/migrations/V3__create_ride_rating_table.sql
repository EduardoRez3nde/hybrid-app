CREATE TABLE tb_ride_rating (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ride_id UUID NOT NULL,
    driver_id UUID NOT NULL,
    passenger_id UUID NOT NULL,
    rating INTEGER NOT NULL,
    comment TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),

    -- Adiciona uma chave estrangeira para garantir a integridade referencial
    CONSTRAINT fk_ride
        FOREIGN KEY(ride_id)
            REFERENCES tb_rides(id),

    -- Garante que a nota esteja sempre entre 1 e 5, como na entidade
    CONSTRAINT chk_rating CHECK (rating >= 1 AND rating <= 5)
);