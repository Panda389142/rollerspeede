package com.rollerspeed.rollerspeed.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Email(message = "El formato del correo no es válido")
    private String email;

    @Column(nullable = false)
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    @Column(nullable = false)
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Column(nullable = false)
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Genero genero;

    @Column(nullable = false)
    @Pattern(regexp = "^[0-9]{10,15}$", message = "El teléfono debe tener entre 10 y 15 dígitos")
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MedioPago medioPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol = Rol.ALUMNO;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean activo = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "usuario_clases",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "clase_id")
    )
    private Set<Clase> clases = new HashSet<>();

    // Constructores
    public Usuario() {}

    public Usuario(String email, String password, String nombre, LocalDate fechaNacimiento, 
                   Genero genero, String telefono, MedioPago medioPago) {
        this.email = email;
        this.password = password;
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.genero = genero;
        this.telefono = telefono;
        this.medioPago = medioPago;
    }

    // Implementación UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return activo;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public Genero getGenero() { return genero; }
    public void setGenero(Genero genero) { this.genero = genero; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public MedioPago getMedioPago() { return medioPago; }
    public void setMedioPago(MedioPago medioPago) { this.medioPago = medioPago; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public Set<Clase> getClases() {
        return clases;
    }

    public void setClases(Set<Clase> clases) {
        this.clases = clases;
    }

    // Enums
    public enum Genero {
        MASCULINO, FEMENINO, OTRO
    }

    public enum MedioPago {
        EFECTIVO, TARJETA_CREDITO, TARJETA_DEBITO, PSE, NEQUI, DAVIPLATA
    }

    public enum Rol {
        ALUMNO, INSTRUCTOR, ADMINISTRADOR
    }
}