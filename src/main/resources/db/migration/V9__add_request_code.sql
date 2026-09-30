CREATE SEQUENCE solicitud_codigo_seq START WITH 1;

ALTER TABLE solicitud
    ADD COLUMN codigo VARCHAR(20);

WITH solicitudes_numeradas AS (
    SELECT id, ROW_NUMBER() OVER (ORDER BY id) AS numero
    FROM solicitud
)
UPDATE solicitud s
SET codigo = 'UGE-' || LPAD(n.numero::TEXT, 4, '0')
FROM solicitudes_numeradas n
WHERE s.id = n.id;

ALTER TABLE solicitud
    ALTER COLUMN codigo SET NOT NULL,
    ADD CONSTRAINT uq_solicitud_codigo UNIQUE (codigo);

SELECT setval(
    'solicitud_codigo_seq',
    GREATEST(COUNT(*), 1),
    COUNT(*) > 0
)
FROM solicitud;