CREATE TABLE rol (
    id BIGSERIAL PRIMARY KEY,
    nombre_rol VARCHAR(20) NOT NULL UNIQUE CHECK (nombre_rol IN ('FUNCIONARIO', 'TECNICO', 'SUPERVISOR'))
);

CREATE TABLE estado_solicitud (
    id BIGSERIAL PRIMARY KEY,
    estado_solicitud VARCHAR(20) NOT NULL UNIQUE CHECK (estado_solicitud IN ('PENDIENTE', 'EN_PROCESO', 'CONCLUIDA')),
    descripcion VARCHAR(70),
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE usuario (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100),
    apellido VARCHAR(100),
    user_login VARCHAR(30) NOT NULL UNIQUE,
    password VARCHAR(30) NOT NULL,
    cargo VARCHAR(20),
    telefono VARCHAR(8),
    area VARCHAR(40),
    ubicacion_oficina VARCHAR(40),
    rol_id BIGINT NOT NULL REFERENCES rol(id),
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE solicitud (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    area VARCHAR(150) NOT NULL,
    categoria VARCHAR(100) NOT NULL,
    descripcion TEXT NOT NULL,
    ubicacion VARCHAR(150),
    equipo_danado VARCHAR(150),
    estado_id BIGINT NOT NULL REFERENCES estado_solicitud(id),
    prioridad VARCHAR(10) CHECK (prioridad IN ('ALTA', 'MEDIA', 'BAJA')),
    solicitante_id BIGINT NOT NULL REFERENCES usuario(id),
    verificado_por_usuario BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE asignacion_tecnico (
    id BIGSERIAL PRIMARY KEY,
    solicitud_id BIGINT NOT NULL REFERENCES solicitud(id),
    tecnico_id BIGINT NOT NULL REFERENCES usuario(id),
    asignado_at TIMESTAMP NOT NULL DEFAULT now(),
    UNIQUE (solicitud_id, tecnico_id)
);

CREATE TABLE reporte_trabajo (
    id BIGSERIAL PRIMARY KEY,
    solicitud_id BIGINT NOT NULL REFERENCES solicitud(id),
    asignacion_tecnico_id BIGINT NOT NULL REFERENCES asignacion_tecnico(id),
    tipo_equipo VARCHAR(150),
    diagnostico TEXT,
    trabajo_realizado TEXT,
    estado_final VARCHAR(30) CHECK (estado_final IN ('SOLUCIONADO', 'PARCIAL', 'PENDIENTE', 'REQUIERE_REPUESTO', 'SERVICIO_EXTERNO')),
    observacion TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE aviso(
    id BIGSERIAL PRIMARY KEY,
    id_supervisor BIGINT NOT NULL REFERENCES usuario(id),
    mensaje TEXT NOT NULL,
    destinatario VARCHAR(15),
    created_at TIMESTAMP NOT NULL DEFAULT now()
);
