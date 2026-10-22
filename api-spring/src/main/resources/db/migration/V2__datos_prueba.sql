-- Datos de prueba ficticios.

INSERT INTO organizacion (nombre, sector, contacto) VALUES
    ('Tecnologias Veracruz', 'Software', 'contacto@tecver.example'),
    ('Hospital Regional Demo', 'Salud', 'rh@hospital.example'),
    ('Logistica del Golfo', 'Transporte', 'practicas@logigolfo.example'),
    ('Banco Ficticio MX', 'Finanzas', 'talento@banco.example');

INSERT INTO estudiante_ficticio (nombre, programa, correo) VALUES
    ('Ana Lopez Prueba', 'Ingenieria de Software', 'ana@estudiante.example'),
    ('Luis Hernandez Demo', 'Redes y Servicios', 'luis@estudiante.example'),
    ('Maria Torres Ficticia', 'Ingenieria de Software', 'maria@estudiante.example'),
    ('Carlos Ruiz Ejemplo', 'Ciencia de Datos', 'carlos@estudiante.example');

INSERT INTO practica (organizacion_id, estudiante_id, titulo, estado)
SELECT o.id, e.id, v.titulo, v.estado
FROM (VALUES
    ('Tecnologias Veracruz', 'Ana Lopez Prueba', 'Desarrollo de API REST', 'EN_CURSO'),
    ('Hospital Regional Demo', 'Luis Hernandez Demo', 'Soporte de infraestructura de red', 'ACEPTADA'),
    ('Logistica del Golfo', 'Maria Torres Ficticia', 'Panel de seguimiento de envios', 'POSTULADA'),
    ('Banco Ficticio MX', 'Carlos Ruiz Ejemplo', 'Analisis de datos de transacciones', 'CONCLUIDA'),
    ('Tecnologias Veracruz', 'Maria Torres Ficticia', 'Pruebas automatizadas', 'RECHAZADA')
) AS v(org, est, titulo, estado)
JOIN organizacion o ON o.nombre = v.org
JOIN estudiante_ficticio e ON e.nombre = v.est;
