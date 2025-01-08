package com.bezkoder.spring.security.postgresql.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "consultas")
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY) // Relación uno a uno con FichaNueva
    @JoinColumn(name = "ficha_id", nullable = false, unique = true) // Una consulta tiene una ficha
    private FichaNueva ficha; // Relación con FichaNueva

    private String sintomas;
    private String diagnostico;
    private String tratamiento;
    private String notas;

    @Column(name = "fecha_consulta", nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "America/La_Paz")
    private ZonedDateTime fechaConsulta;

    // Método para establecer la fecha de consulta al momento de persistir
    @PrePersist
    public void prePersist() {
        ZonedDateTime fechaUtc = ZonedDateTime.now(ZoneId.of("UTC"));
        // Convertir la fecha UTC a la zona horaria de Bolivia (America/La_Paz), que es GMT-4
        fechaConsulta = fechaUtc.withZoneSameInstant(ZoneId.of("America/La_Paz"));
    }

    // Constructor, getters, setters, etc.
    public Consulta() {
        // Constructor vacío
    }

    public Consulta(FichaNueva ficha, String sintomas, String diagnostico, String tratamiento, String notas) {
        this.ficha = ficha;
        this.sintomas = sintomas;
        this.diagnostico = diagnostico;
        this.tratamiento = tratamiento;
        this.notas = notas;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FichaNueva getFicha() {
        return ficha;
    }

    public void setFicha(FichaNueva ficha) {
        this.ficha = ficha;
    }

    public String getSintomas() {
        return sintomas;
    }

    public void setSintomas(String sintomas) {
        this.sintomas = sintomas;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(String tratamiento) {
        this.tratamiento = tratamiento;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public ZonedDateTime getFechaConsulta() {
        return fechaConsulta;
    }

    public void setFechaConsulta(ZonedDateTime fechaConsulta) {
        this.fechaConsulta = fechaConsulta;
    }


}
