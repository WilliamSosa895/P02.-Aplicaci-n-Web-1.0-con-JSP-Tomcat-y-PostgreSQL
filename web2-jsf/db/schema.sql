-- ============================================================================
-- P03 / M03 - Incremento Web 2.0 (JSF + PrimeFaces) - Proyecto PR10
-- Motor: PostgreSQL 16
-- Tabla del modulo web2. "name" es obligatorio (contrato de verify-module.sh M03);
-- "estado" y "detalle" son opcionales para el flujo de dominio (postulaciones).
-- Idempotente. La aplicacion tambien crea esta tabla al arrancar (RegistroRepository.initialize).
-- Solo datos ficticios; sin credenciales en el repositorio.
-- ============================================================================
CREATE TABLE IF NOT EXISTS web2_record (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    estado     VARCHAR(20),
    detalle    VARCHAR(300),
    creado_en  TIMESTAMP NOT NULL DEFAULT now()
);
