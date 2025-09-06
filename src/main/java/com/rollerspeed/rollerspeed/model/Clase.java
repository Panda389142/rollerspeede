package com.rollerspeed.rollerspeed.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "clases")
public class Clase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "El nombre de la clase es obligatorio")
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Nivel nivel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiaSemana diaSemana;

    @Column(nullable = false)
    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    @Column(nullable = false)
    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;

    @Column(nullable = false)
    @Min(value = 1, message = "La capacidad máxima debe ser al menos 1")
    @Max(value = 50, message = "La capacidad máxima no puede exceder 50")
    private Integer capacidadMaxima;

    @Column
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    private Double precio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id")
    private Usuario instructor;

    @ManyToMany(mappedBy = "clases", fetch = FetchType.LAZY)
    private Set<Usuario> alumnos = new HashSet<>();

    @OneToMany(mappedBy = "clase", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Asistencia> asistencias = new HashSet<>();

    @Column(nullable = false)
    private Boolean activa = true;

    // Constructores
    public Clase() {}

    public Clase(String nombre, String descripcion, Nivel nivel, DiaSemana diaSemana,
                LocalTime horaInicio, LocalTime horaFin, Integer capacidadMaxima, Usuario instructor) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.nivel = nivel;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.capacidadMaxima = capacidadMaxima;
        this.instructor = instructor;
    }

    // Métodos de utilidad
    public boolean tieneCupo() {
        return alumnos.size() < capacidadMaxima;
    }

    public int getCuposDisponibles() {
        return capacidadMaxima - alumnos.size();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Nivel getNivel() { return nivel; }
    public void setNivel(Nivel nivel) { this.nivel = nivel; }

    public DiaSemana getDiaSemana() { return diaSemana; }
    public void setDiaSemana(DiaSemana diaSemana) { this.diaSemana = diaSemana; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    public Integer getCapacidadMaxima() { return capacidadMaxima; }
    public void setCapacidadMaxima(Integer capacidadMaxima) { this.capacidadMaxima = capacidadMaxima; }

    public Usuario getInstructor() { return instructor; }
    public void setInstructor(Usuario instructor) { this.instructor = instructor; }

    public Set<Usuario> getAlumnos() { return alumnos; }
    public void setAlumnos(Set<Usuario> alumnos) { this.alumnos = alumnos; }

    public Set<Asistencia> getAsistencias() { return asistencias; }
    public void setAsistencias(Set<Asistencia> asistencias) { this.asistencias = asistencias; }

    public Boolean getActiva() { return activa; }
    public void setActiva(Boolean activa) { this.activa = activa; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    // Enums
    public enum Nivel {
        PRINCIPIANTE("Principiante"),
        INTERMEDIO("Intermedio"),
        AVANZADO("Avanzado"),
        COMPETITIVO("Competitivo");

        private final String descripcion;

        Nivel(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    public enum DiaSemana {
        LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO, DOMINGO
    }
}