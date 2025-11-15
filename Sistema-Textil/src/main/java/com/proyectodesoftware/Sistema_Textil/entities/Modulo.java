package com.proyectodesoftware.Sistema_Textil.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "modulos")
public class Modulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_modulo", nullable = false, unique = true)
    private Integer numeroModulo;

    @Column(name = "nombre_modulo", nullable = false, length = 100)
    private String nombreModulo;

    // ✅ AGREGAR CAMPOS FALTANTES
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "supervisor_id")
    private Long supervisorId;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    // Lado "managed" de la relación
    @OneToMany(mappedBy = "modulo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Produccion> producciones;

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
    }

    // Constructores
    public Modulo() {}

    public Modulo(Integer numeroModulo, String nombreModulo, String descripcion, Long supervisorId) {
        this.numeroModulo = numeroModulo;
        this.nombreModulo = nombreModulo;
        this.descripcion = descripcion;
        this.supervisorId = supervisorId;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumeroModulo() {
        return numeroModulo;
    }

    public void setNumeroModulo(Integer numeroModulo) {
        this.numeroModulo = numeroModulo;
    }

    public String getNombreModulo() {
        return nombreModulo;
    }

    public void setNombreModulo(String nombreModulo) {
        this.nombreModulo = nombreModulo;
    }

    public List<Produccion> getProducciones() {
        return producciones;
    }

    public void setProducciones(List<Produccion> producciones) {
        this.producciones = producciones;
    }

    // ✅ AGREGAR GETTERS Y SETTERS NUEVOS
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(Long supervisorId) {
        this.supervisorId = supervisorId;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
}