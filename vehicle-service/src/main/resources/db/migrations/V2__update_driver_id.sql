ALTER TABLE vehicles
ALTER COLUMN driver_id TYPE uuid USING driver_id::uuid;
