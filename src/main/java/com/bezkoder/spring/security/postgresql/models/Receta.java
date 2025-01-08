
package com.bezkoder.spring.security.postgresql.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
        import java.time.ZonedDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "recetas")
public class Receta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Medicamentos recetados
    @Column(nullable = false)
    private String medicamentos;

    // Indicaciones para el paciente
    @Column(nullable = false)
    private String indicaciones;


    // Relación con Consulta (relación de uno a uno)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consulta_id", nullable = false, unique = true) // Relación uno a uno con consulta
    @JsonBackReference // Evita la serialización en este lado de la relación
    private Consulta consulta;

    // Fecha y hora de la receta, registrada automáticamente al completar
    @Column(name = "fecha", nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "America/La_Paz")
    private ZonedDateTime fechaConsulta;

    // Método para asignar automáticamente la fecha y hora con zona horaria de Bolivia
    @PrePersist
    public void prePersist() {
        ZonedDateTime fechaUtc = ZonedDateTime.now(ZoneId.of("UTC"));
        // Convertir la fecha UTC a la zona horaria de Bolivia (America/La_Paz), que es GMT-4
        fechaConsulta = fechaUtc.withZoneSameInstant(ZoneId.of("America/La_Paz"));
    }

    // Constructor vacío
    public Receta() {}

    // Constructor
    public Receta(String medicamentos, String indicaciones, Consulta consulta) {
        this.medicamentos = medicamentos;
        this.indicaciones = indicaciones;
        this.consulta = consulta;
    }

    // Métodos getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(String medicamentos) {
        this.medicamentos = medicamentos;
    }

    public String getIndicaciones() {
        return indicaciones;
    }

    public void setIndicaciones(String indicaciones) {
        this.indicaciones = indicaciones;
    }

    public ZonedDateTime getFechaConsulta() {
        return fechaConsulta;
    }

    public void setFechaConsulta(ZonedDateTime fechaConsulta) {
        this.fechaConsulta = fechaConsulta;
    }

    public Consulta getConsulta() {
        return consulta;
    }

    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
    }


}
