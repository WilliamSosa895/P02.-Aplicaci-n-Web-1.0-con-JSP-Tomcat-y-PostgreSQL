-- ============================================================================
-- PR10 - Gestion de practicas profesionales - Modelo relacional (P04, Web 3.0)
-- Motor: PostgreSQL 16. Modelo completo de 6 entidades del banco C11, coherente
-- con P02. La API REST (Spring Boot) expone la practica como recurso principal.
-- La aplicacion puede crear el esquema por JPA; este script lo documenta y
-- permite prepararlo manualmente. Solo datos ficticios; sin credenciales.
-- ============================================================================

-- 1. Organizaciones que ofertan practicas
CREATE TABLE IF NOT EXISTS organizacion (
    id            BIGSERIAL PRIMARY KEY,
    nombre        VARCHAR(100) NOT NULL,
    sector        VARCHAR(80),
    contacto      VARCHAR(120),
    CONSTRAINT uq_organizacion_nombre UNIQUE (nombre),
    CONSTRAINT ck_organizacion_nombre_min CHECK (char_length(btrim(nombre)) >= 3)
);

-- 2. Estudiantes ficticios que se postulan
CREATE TABLE IF NOT EXISTS estudiante_ficticio (
    id            BIGSERIAL PRIMARY KEY,
    nombre        VARCHAR(100) NOT NULL,
    programa      VARCHAR(100),
    correo        VARCHAR(120),
    CONSTRAINT uq_estudiante_correo UNIQUE (correo),
    CONSTRAINT ck_estudiante_nombre_min CHECK (char_length(btrim(nombre)) >= 3)
);

-- 3. Practica: recurso principal. Relaciona estudiante y organizacion.
CREATE TABLE IF NOT EXISTS practica (
    id               BIGSERIAL PRIMARY KEY,
    organizacion_id  BIGINT NOT NULL REFERENCES organizacion(id) ON DELETE RESTRICT,
    estudiante_id    BIGINT NOT NULL REFERENCES estudiante_ficticio(id) ON DELETE RESTRICT,
    titulo           VARCHAR(120) NOT NULL,
    estado           VARCHAR(20)  NOT NULL DEFAULT 'POSTULADA',
    creada_en        TIMESTAMP    NOT NULL DEFAULT now(),
    CONSTRAINT ck_practica_titulo_min CHECK (char_length(btrim(titulo)) >= 3),
    CONSTRAINT ck_practica_estado
        CHECK (estado IN ('POSTULADA','ACEPTADA','EN_CURSO','CONCLUIDA','RECHAZADA')),
    CONSTRAINT uq_practica_postulacion UNIQUE (organizacion_id, estudiante_id, titulo)
);

-- 4. Avance sobre una practica
CREATE TABLE IF NOT EXISTS avance (
    id            BIGSERIAL PRIMARY KEY,
    practica_id   BIGINT NOT NULL REFERENCES practica(id) ON DELETE CASCADE,
    descripcion   VARCHAR(300) NOT NULL,
    porcentaje    INTEGER NOT NULL DEFAULT 0,
    registrado_en TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT ck_avance_porcentaje CHECK (porcentaje BETWEEN 0 AND 100)
);

-- 5. Evidencia asociada a un avance
CREATE TABLE IF NOT EXISTS evidencia (
    id            BIGSERIAL PRIMARY KEY,
    avance_id     BIGINT NOT NULL REFERENCES avance(id) ON DELETE CASCADE,
    tipo          VARCHAR(40) NOT NULL,
    referencia    VARCHAR(200),
    nota          VARCHAR(300)
);

-- 6. Evento de plazo simulado (fuente controlada; base del incremento Web 4.0)
CREATE TABLE IF NOT EXISTS evento_plazo (
    id            BIGSERIAL PRIMARY KEY,
    practica_id   BIGINT NOT NULL REFERENCES practica(id) ON DELETE CASCADE,
    tipo          VARCHAR(40) NOT NULL,
    instante      TIMESTAMP NOT NULL DEFAULT now(),
    unidad        VARCHAR(20),
    valor         VARCHAR(60),
    simulado      BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX IF NOT EXISTS ix_practica_organizacion ON practica(organizacion_id);
CREATE INDEX IF NOT EXISTS ix_practica_estudiante   ON practica(estudiante_id);
CREATE INDEX IF NOT EXISTS ix_avance_practica       ON avance(practica_id);
