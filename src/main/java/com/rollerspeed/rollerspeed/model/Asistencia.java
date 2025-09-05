package com.rollerspeed.rollerspeed.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "asistencias", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"alumno_id", "clase_id", "fecha"}))
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alumno_id", nullable = false)
    @NotNull(message = "El alumno es obligatorio")
    private Usuario alumno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clase_id", nullable = false)
    @NotNull(message = "La clase es obligatoria")
    private Clase clase;

    @Column(nullable = false)
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @Column(nullable = false)
    private Boolean presente = false;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registrado_por_id")
    private Usuario registradoPor;

    // Constructores
    public Asistencia() {}

    public Asistencia(Usuario alumno, Clase clase, LocalDate fecha, Boolean presente, 
                     String observaciones, Usuario registradoPor) {
        this.alumno = alumno;
        this.clase = clase;
        this.fecha = fecha;
        this.presente = presente;
        this.observaciones = observaciones;
        this.registradoPor = registradoPor;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getAlumno() { return alumno; }
    public void setAlumno(Usuario alumno) { this.alumno = alumno; }

    public Clase getClase() { return clase; }
    public void setClase(Clase clase) { this.clase = clase; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public Boolean getPresente() { return presente; }
    public void setPresente(Boolean presente) { this.presente = presente; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public Usuario getRegistradoPor() { return registradoPor; }
    public void setRegistradoPor(Usuario registradoPor) { this.registradoPor = registradoPor; }
}