ALTER TABLE estado_solicitud
    DROP CONSTRAINT IF EXISTS estado_solicitud_estado_solicitud_check;

ALTER TABLE estado_solicitud
    ADD CONSTRAINT ck_estado_solicitud_valid
    CHECK (estado_solicitud IN ('PENDIENTE', 'EN_PROCESO', 'CONCLUIDA', 'RECHAZADA'));

INSERT INTO estado_solicitud (estado_solicitud, descripcion)
VALUES ('RECHAZADA', 'Solicitud rechazada por el técnico')
ON CONFLICT (estado_solicitud) DO NOTHING;

ALTER TABLE solicitud
    ADD COLUMN motivo_rechazo VARCHAR(500);
