ALTER TABLE usuario
    ADD COLUMN primer_ingreso BOOLEAN NOT NULL DEFAULT TRUE;

UPDATE usuario
SET primer_ingreso = FALSE
WHERE user_login = 'supervisor';