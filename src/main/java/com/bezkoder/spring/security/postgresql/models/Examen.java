package com.bezkoder.spring.security.postgresql.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "examenes")
public class Examen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre del laboratorio
    @Column(nullable = false)
    private String laboratorioNombre;

    // Tipo de examen realizado
    @Column(nullable = false)
    private String tipoExamen;

    // Resultados del examen
    @Column(nullable = false)
    private String resultados;

    // Fecha y hora del examen con zona horaria (la fecha se asignará automáticamente)
    @Column(name = "fecha_examen", nullable = false, updatable = false)
    private String fechaExamen;

    // Relación con la Consulta
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consulta_id", nullable = false)
    @JsonBackReference
    private Consulta consulta;


    // Constructor vacío
    public Examen() {}

    // Constructor con todos los campos (sin los archivos)
    public Examen(String laboratorioNombre, String tipoExamen, String resultados, Consulta consulta) {
        this.laboratorioNombre = laboratorioNombre;
        this.tipoExamen = tipoExamen;
        this.resultados = resultados;
        this.consulta = consulta;
    }

    // Métodos getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLaboratorioNombre() {
        return laboratorioNombre;
    }

    public void setLaboratorioNombre(String laboratorioNombre) {
        this.laboratorioNombre = laboratorioNombre;
    }

    public String getTipoExamen() {
        return tipoExamen;
    }

    public void setTipoExamen(String tipoExamen) {
        this.tipoExamen = tipoExamen;
    }

    public String getResultados() {
        return resultados;
    }

    public void setResultados(String resultados) {
        this.resultados = resultados;
    }

    public Consulta getConsulta() {
        return consulta;
    }

    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
    }
    public String getFechaExamen() {
        return fechaExamen;
    }

    public void setFechaExamen(String fechaExamen) {
        this.fechaExamen = fechaExamen;
    }
}
