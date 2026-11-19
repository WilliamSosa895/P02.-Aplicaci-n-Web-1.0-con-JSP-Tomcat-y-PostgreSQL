-- P06 (Web 4.0, IoT): enriquece la telemetria con variable, unidad y calidad.
-- Compatible con las lecturas existentes (columnas nuevas opcionales).
ALTER TABLE telemetry ADD COLUMN IF NOT EXISTS variable VARCHAR(40);
ALTER TABLE telemetry ADD COLUMN IF NOT EXISTS unit     VARCHAR(16);
ALTER TABLE telemetry ADD COLUMN IF NOT EXISTS quality  VARCHAR(16);

-- Indices para el historial temporal y el dashboard.
CREATE INDEX IF NOT EXISTS ix_telemetry_device_time ON telemetry(device_id, captured_at DESC);
CREATE INDEX IF NOT EXISTS ix_telemetry_quality     ON telemetry(quality);
