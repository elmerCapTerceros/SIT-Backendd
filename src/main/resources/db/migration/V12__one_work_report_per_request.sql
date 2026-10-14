ALTER TABLE reporte_trabajo
    ADD CONSTRAINT uq_reporte_trabajo_solicitud UNIQUE (solicitud_id);
