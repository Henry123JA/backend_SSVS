
package com.bezkoder.spring.security.postgresql.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
        import java.util.Date;

@Entity
@Table(name = "tratamientos")
public class Tratamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con Consulta
    @ManyToOne
    @JoinColumn(name = "consulta_id", nullable = false)
    @JsonBackReference
    private Consulta consulta;

    // Descripción del tratamiento
    @Column(nullable = false)
    private String descripcion;

    // Fecha de inicio del tratamiento
    @Temporal(TemporalType.DATE)
    private Date fechaInicio;

    // Fecha de fin del tratamiento
    @Temporal(TemporalType.DATE)
    private Date fechaFin;

    // Seguimiento del tratamiento
    @Column(nullable = true)
    private String seguimiento;

    // Constructor vacío
    public Tratamiento() {}

    // Constructor
    public Tratamiento(Consulta consulta, String descripcion, Date fechaInicio, Date fechaFin, String seguimiento) {
        this.consulta = consulta;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.seguimiento = seguimiento;
    }

    // Métodos getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Consulta getConsulta() {
        return consulta;
    }

    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getSeguimiento() {
        return seguimiento;
    }

    public void setSeguimiento(String seguimiento) {
        this.seguimiento = seguimiento;
    }
}
