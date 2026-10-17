ALTER TABLE reporte_trabajo
    ADD COLUMN numero_formulario VARCHAR(50),
    ADD COLUMN fecha_atencion DATE,
    ADD COLUMN hora_atencion TIME,
    ADD COLUMN unidad VARCHAR(150),
    ADD COLUMN solicitante VARCHAR(200),
    ADD COLUMN numero_inventario VARCHAR(100),
    ADD COLUMN tipo_equipo_otro VARCHAR(150),
    ADD COLUMN problema TEXT,
    ADD COLUMN tecnico_nombre VARCHAR(200),
    ADD COLUMN funcionario_nombre VARCHAR(200);
