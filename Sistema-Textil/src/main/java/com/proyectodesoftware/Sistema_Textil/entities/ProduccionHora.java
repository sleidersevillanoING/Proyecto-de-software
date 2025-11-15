package com.proyectodesoftware.Sistema_Textil.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "produccion_hora")
public class ProduccionHora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "produccion_id", foreignKey = @ForeignKey(name = "fk_produccionhora_produccion"))
    private Produccion produccion;

    @Column(nullable = false)
    private LocalTime hora;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer cantidad = 0;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer defectuosos = 0;

    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
    }

    public ProduccionHora() {}

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Produccion getProduccion() { return produccion; }
    public void setProduccion(Produccion produccion) { this.produccion = produccion; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Integer getDefectuosos() { return defectuosos; }
    public void setDefectuosos(Integer defectuosos) { this.defectuosos = defectuosos; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}
