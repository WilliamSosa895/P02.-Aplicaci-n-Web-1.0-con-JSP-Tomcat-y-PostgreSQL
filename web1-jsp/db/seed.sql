-- ============================================================================
-- PR10 - Datos de referencia ficticios para P02 (Web 1.0)
-- Solo datos sinteticos. Ejecutar despues de schema.sql.
-- Idempotente: usa ON CONFLICT para no duplicar al reejecutar.
-- ============================================================================

INSERT INTO organizacion (nombre, sector, contacto) VALUES
    ('Soluciones Verdes SA (ficticia)', 'Tecnologia ambiental', 'contacto.ficticio@ejemplo.test'),
    ('Consultora Delta (ficticia)',     'Consultoria TI',       'rrhh.ficticio@ejemplo.test'),
    ('Cooperativa Norte (ficticia)',    'Logistica',            'enlace.ficticio@ejemplo.test')
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO estudiante_ficticio (nombre, programa, correo) VALUES
    ('Ana Estudiante Ficticia',   'Ingenieria de Software', 'ana.ficticia@ejemplo.test'),
    ('Luis Estudiante Ficticio',  'Ingenieria de Software', 'luis.ficticio@ejemplo.test'),
    ('Sara Estudiante Ficticia',  'Redes y Telecomunicaciones', 'sara.ficticia@ejemplo.test')
ON CONFLICT (correo) DO NOTHING;

-- Nota: las postulaciones (tabla practica) se generan desde la aplicacion
-- (POST /postulacion). El GET inicial muestra el catalogo de postulaciones vacio
-- hasta que se registra la primera, lo que permite demostrar el flujo completo.
