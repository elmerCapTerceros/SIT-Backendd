
INSERT INTO usuario (
    nombre,
    apellido,
    user_login,
    password,
    cargo,
    rol_id
)
SELECT
    'Tecnico',
    'Inicial',
    'tecnico1',
    crypt('PASSWORD', gen_salt('bf', 10)),
    'Tecnico',
    r.id
FROM rol r
WHERE r.nombre_rol = 'TECNICO'
  AND NOT EXISTS (
      SELECT 1
      FROM usuario u
      WHERE u.user_login = 'tecnico1'
  );


INSERT INTO usuario (
    nombre,
    apellido,
    user_login,
    password,
    cargo,
    rol_id
)
SELECT
    'Funcionario',
    'Inicial',
    'funcionario1',
    crypt('PASSWORD', gen_salt('bf', 10)),
    'Funcionario',
    r.id
FROM rol r
WHERE r.nombre_rol = 'FUNCIONARIO'
  AND NOT EXISTS (
      SELECT 1
      FROM usuario u
      WHERE u.user_login = 'funcionario1'
  );