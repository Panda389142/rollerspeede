CREATE TABLE galeria_items (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descripcion TEXT,
    image_url VARCHAR(255) NOT NULL,
    video_url VARCHAR(255),
    tipo VARCHAR(50) NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL
);
