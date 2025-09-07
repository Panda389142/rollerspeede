-- V1__Initial_schema.sql

-- Create tables

CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    genero VARCHAR(255) NOT NULL,
    telefono VARCHAR(15) NOT NULL,
    medio_pago VARCHAR(255) NOT NULL,
    rol VARCHAR(255) NOT NULL,
    fecha_registro TIMESTAMP NOT NULL,
    activo BOOLEAN NOT NULL
);

CREATE TABLE clases (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    nivel VARCHAR(255) NOT NULL,
    dia_semana VARCHAR(255) NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    capacidad_maxima INT NOT NULL,
    precio DOUBLE PRECISION,
    instructor_id BIGINT,
    activa BOOLEAN NOT NULL
);

CREATE TABLE usuario_clases (
    usuario_id BIGINT NOT NULL,
    clase_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, clase_id)
);

CREATE TABLE pagos (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    clase_id BIGINT,
    monto NUMERIC(10, 2) NOT NULL,
    estado VARCHAR(255) NOT NULL,
    medio_pago VARCHAR(255) NOT NULL,
    fecha_generacion DATE NOT NULL,
    fecha_pago TIMESTAMP,
    descripcion TEXT
);

CREATE TABLE asistencias (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    clase_id BIGINT NOT NULL,
    fecha DATE NOT NULL,
    presente BOOLEAN NOT NULL,
    observaciones TEXT,
    fecha_registro TIMESTAMP NOT NULL,
    registrado_por_id BIGINT,
    UNIQUE (usuario_id, clase_id, fecha)
);

CREATE TABLE eventos (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descripcion TEXT NOT NULL,
    fecha_evento TIMESTAMP NOT NULL,
    activo BOOLEAN NOT NULL
);

CREATE TABLE noticias (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    contenido TEXT NOT NULL,
    fecha_publicacion TIMESTAMP NOT NULL,
    activo BOOLEAN NOT NULL,
    autor_id BIGINT
);

CREATE TABLE testimonios (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    comentario TEXT NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL,
    activo BOOLEAN NOT NULL
);

-- Add foreign key constraints

ALTER TABLE clases
ADD CONSTRAINT fk_clases_on_instructor FOREIGN KEY (instructor_id) REFERENCES usuarios (id);

ALTER TABLE usuario_clases
ADD CONSTRAINT fk_usucl_on_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id);

ALTER TABLE usuario_clases
ADD CONSTRAINT fk_usucl_on_clase FOREIGN KEY (clase_id) REFERENCES clases (id);

ALTER TABLE pagos
ADD CONSTRAINT fk_pagos_on_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id);

ALTER TABLE pagos
ADD CONSTRAINT fk_pagos_on_clase FOREIGN KEY (clase_id) REFERENCES clases (id);

ALTER TABLE asistencias
ADD CONSTRAINT fk_asistencias_on_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id);

ALTER TABLE asistencias
ADD CONSTRAINT fk_asistencias_on_clase FOREIGN KEY (clase_id) REFERENCES clases (id);

ALTER TABLE asistencias
ADD CONSTRAINT fk_asistencias_on_registrado_por FOREIGN KEY (registrado_por_id) REFERENCES usuarios (id);

ALTER TABLE noticias
ADD CONSTRAINT fk_noticias_on_autor FOREIGN KEY (autor_id) REFERENCES usuarios (id);