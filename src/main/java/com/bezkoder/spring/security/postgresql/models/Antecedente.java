
package com.bezkoder.spring.security.postgresql.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
        import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
@Table(name = "antecedentes")
public class Antecedente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con Paciente
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    // Antecedentes personales
    @Column(name = "enfermedades_base", nullable = true, length = 500)
    private String enfermedadesBase;

    // Antecedentes familiares
    @Column(name = "enfermedades_hereditarias", nullable = true, length = 500)
    private String enfermedadesHereditarias;

    // Antecedentes personales no patológicos
    @Column(name = "otras", nullable = true, length = 500)
    private String otras;

    // Fecha de creación de los antecedentes
    @Column(name = "fecha", nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "America/La_Paz")
    private ZonedDateTime fecha;

    // Método para obtener la fecha actual en la zona horaria de La Paz
    @PrePersist
    public void prePersist() {
        ZonedDateTime fechaUtc = ZonedDateTime.now(ZoneId.of("UTC"));
        // Convertir la fecha UTC a la zona horaria de Bolivia (America/La_Paz), que es GMT-4
        fecha = fechaUtc.withZoneSameInstant(ZoneId.of("America/La_Paz"));
    }

    // Métodos getter y setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public String getEnfermedadesBase() {
        return enfermedadesBase;
    }

    public void setEnfermedadesBase(String enfermedadesBase) {
        this.enfermedadesBase = enfermedadesBase;
    }

    public String getEnfermedadesHereditarias() {
        return enfermedadesHereditarias;
    }

    public void setEnfermedadesHereditarias(String enfermedadesHereditarias) {
        this.enfermedadesHereditarias = enfermedadesHereditarias;
    }

    public String getOtras() {
        return otras;
    }

    public void setOtras(String otras) {
        this.otras = otras;
    }

    public ZonedDateTime getFecha() {
        return fecha;
    }

    public void setFecha(ZonedDateTime fecha) {
        this.fecha = fecha;
    }
}
