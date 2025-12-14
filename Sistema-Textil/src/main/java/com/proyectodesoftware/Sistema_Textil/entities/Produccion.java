package com.proyectodesoftware.Sistema_Textil.entities;


import jakarta.persistence.*;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;
@Entity
@Table(name = "producciones")
public class Produccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String referencia;

    @Column
    private LocalDate fecha;
    @Column
    private String marca;
    
    @Column
    private String estilo;

    @Column(name = "produccion_total")
    private Integer produccionTotal = 0;

        @Column(name = "creado_por")
    private Long creadoPor;
        
    @Column(name = "defectuosos_total")
    private Integer defectuososTotal = 0;

    @Column(nullable = false)
    private Integer cantidadTotal;


    // Relación con Modulo - lado "back" de la relación
    @ManyToOne
    @JoinColumn(name = "modulo_id")
    @JsonBackReference
    private Modulo modulo;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Integer getProduccionTotal() {
        return produccionTotal;
    }

    public void setProduccionTotal(Integer produccionTotal) {
        this.produccionTotal = produccionTotal;
    }

    public Modulo getModulo() {
        return modulo;
    }

    public void setModulo(Modulo modulo) {
        this.modulo = modulo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getEstilo() {
        return estilo;
    }

    public void setEstilo(String estilo) {
        this.estilo = estilo;
    }

    public Long getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(Long creadoPor) {
        this.creadoPor = creadoPor;
    }

    public Integer getDefectuososTotal() {
        return defectuososTotal;
    }

    public void setDefectuososTotal(Integer defectuososTotal) {
        this.defectuososTotal = defectuososTotal;
    }
     @Transient
    public double getEficiencia() {
        if (produccionTotal == null || produccionTotal == 0) return 0.0;
        return ((produccionTotal - defectuososTotal) * 100.0) / produccionTotal;
    }

    // ✅ MÉTODO PARA FORMATEAR EFICIENCIA
    @Transient
    public String getEficienciaFormateada() {
        return String.format("%.1f%%", getEficiencia());
    }

    public void setCantidadTotal(Integer cantidadTotal) {
        this.cantidadTotal = cantidadTotal;
    }

    
}
