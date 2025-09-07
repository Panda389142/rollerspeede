CREATE TABLE notificaciones (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    mensaje VARCHAR(500) NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL,
    leido BOOLEAN NOT NULL,
    url VARCHAR(255),
    CONSTRAINT fk_notificaciones_on_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
);
