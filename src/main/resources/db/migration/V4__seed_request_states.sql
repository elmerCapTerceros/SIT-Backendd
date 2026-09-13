INSERT INTO estado_solicitud (estado_solicitud, descripcion)
VALUES
    ('PENDIENTE', 'Solicitud creada y pendiente de asignación'),
    ('EN_PROCESO', 'Solicitud asignada a un técnico'),
    ('CONCLUIDA', 'Solicitud atendida y cerrada')
ON CONFLICT (estado_solicitud) DO NOTHING;