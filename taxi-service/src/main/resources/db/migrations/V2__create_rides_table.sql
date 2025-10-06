CREATE TABLE tb_rides (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    passenger_id UUID NOT NULL,
    driver_id UUID,
    vehicle_id UUID,
    status ride_status NOT NULL,
    origin_coordinates GEOGRAPHY(POINT, 4326) NOT NULL,
    destination_coordinates GEOGRAPHY(POINT, 4326) NOT NULL,
    estimated_price NUMERIC(10, 2),
    final_price NUMERIC(10, 2),
    accepted_at TIMESTAMP WITH TIME ZONE,
    started_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE
);