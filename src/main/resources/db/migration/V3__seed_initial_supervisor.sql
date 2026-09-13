CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO rol (nombre_rol)
VALUES ('FUNCIONARIO'), ('TECNICO'), ('SUPERVISOR')
ON CONFLICT (nombre_rol) DO NOTHING;

INSERT INTO usuario (
    nombre,
    apellido,
    user_login,
    password,
    cargo,
    rol_id
)
SELECT
    'Administrador',
    'Inicial',
    'supervisor',
    crypt('password', gen_salt('bf', 10)),
    'Supervisor',
    r.id
FROM rol r
WHERE r.nombre_rol = 'SUPERVISOR'
  AND NOT EXISTS (
      SELECT 1
      FROM usuario u
      WHERE u.user_login = 'supervisor'
  );