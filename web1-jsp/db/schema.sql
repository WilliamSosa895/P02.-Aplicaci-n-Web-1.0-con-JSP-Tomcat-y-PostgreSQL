-- ============================================================================
-- PR10 - Gestion de practicas profesionales
-- Modulo M02 / Actividad P02 - Incremento Web 1.0 (JSP, Servlet, JDBC)
-- Motor: PostgreSQL 16
-- ----------------------------------------------------------------------------
-- Modelo minimo del banco C11 (6 entidades):
--   organizacion, estudiante_ficticio, practica, avance, evidencia, evento_plazo
-- Solo se usan datos ficticios. No se versionan credenciales ni datos reales.
-- Este script es idempotente: puede ejecutarse varias veces sin error.
-- El WAR tambien crea estas tablas al arrancar (ver PracticasRepository.initSchema),
-- por lo que el despliegue en contenedor no depende de ejecutar este archivo a mano.
-- ============================================================================

-- 1. Organizaciones que ofertan practicas (ficticias)
CREATE TABLE IF NOT EXISTS organizacion (
    id            BIGSERIAL PRIMARY KEY,
    nombre        VARCHAR(100) NOT NULL,
    sector        VARCHAR(80),
    contacto      VARCHAR(120),
    creada_en     TIMESTAMP NOT NULL DEFAULT now(),
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

-- 3. Practica: postulacion de un estudiante a una organizacion (flujo principal)
CREATE TABLE IF NOT EXISTS practica (
    id               BIGSERIAL PRIMARY KEY,
    organizacion_id  BIGINT NOT NULL REFERENCES organizacion(id) ON DELETE RESTRICT,
    estudiante_id    BIGINT NOT NULL REFERENCES estudiante_ficticio(id) ON DELETE RESTRICT,
    titulo           VARCHAR(120) NOT NULL,
    plan             VARCHAR(300),
    estado           VARCHAR(20) NOT NULL DEFAULT 'POSTULADA',
    creada_en        TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT ck_practica_estado
        CHECK (estado IN ('POSTULADA','ACEPTADA','EN_CURSO','CONCLUIDA','RECHAZADA')),
    CONSTRAINT uq_practica_postulacion
        UNIQUE (organizacion_id, estudiante_id, titulo)
);

-- 4. Avance registrado sobre una practica
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

-- Indices de apoyo para el catalogo y el listado de postulaciones
CREATE INDEX IF NOT EXISTS ix_practica_organizacion ON practica(organizacion_id);
CREATE INDEX IF NOT EXISTS ix_practica_estudiante   ON practica(estudiante_id);
CREATE INDEX IF NOT EXISTS ix_avance_practica       ON avance(practica_id);
