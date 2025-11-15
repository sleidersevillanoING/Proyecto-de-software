package com.proyectodesoftware.Sistema_Textil.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reportes")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "total_produccion", columnDefinition = "INT DEFAULT 0")
    private Integer totalProduccion = 0;

    @Column(name = "total_defectuosos", columnDefinition = "INT DEFAULT 0")
    private Integer totalDefectuosos = 0;

    @Column(name = "tiempo_promedio_hora", precision = 5, scale = 2)
    private BigDecimal tiempoPromedioHora;

    @ManyToOne
    @JoinColumn(name = "generado_por", foreignKey = @ForeignKey(name = "fk_reporte_usuario"))
    private Usuario generadoPor;

    @Column(name = "generado_en", updatable = false)
    private LocalDateTime generadoEn;

    @PrePersist
    protected void onCreate() {
        this.generadoEn = LocalDateTime.now();
    }

    public Reporte() {}

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public Integer getTotalProduccion() { return totalProduccion; }
    public void setTotalProduccion(Integer totalProduccion) { this.totalProduccion = totalProduccion; }

    public Integer getTotalDefectuosos() { return totalDefectuosos; }
    public void setTotalDefectuosos(Integer totalDefectuosos) { this.totalDefectuosos = totalDefectuosos; }

    public BigDecimal getTiempoPromedioHora() { return tiempoPromedioHora; }
    public void setTiempoPromedioHora(BigDecimal tiempoPromedioHora) { this.tiempoPromedioHora = tiempoPromedioHora; }

    public Usuario getGeneradoPor() { return generadoPor; }
    public void setGeneradoPor(Usuario generadoPor) { this.generadoPor = generadoPor; }

    public LocalDateTime getGeneradoEn() { return generadoEn; }
    public void setGeneradoEn(LocalDateTime generadoEn) { this.generadoEn = generadoEn; }
}
