package com.bezkoder.spring.security.postgresql.models;

import jakarta.persistence.*;
        import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
@Table(name = "fichas_nuevas")
public class FichaNueva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con Paciente
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    // Almacena solo el ID del Médico
    @Column(name = "medico_id", nullable = false)
    private Long medicoId;  // Solo se almacena el ID del médico

    // Relación con Especialidad
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "especialidad_id", nullable = false)
    private Especialidad especialidad;

    // Relación con BloqueHorario
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bloque_horario_id", nullable = false)
    private BloqueHorario bloqueHorario;

    private LocalDate fecha; // Fecha de la cita programada (sin la hora, solo la fecha)
    private LocalTime hora;
    private String estado; // "pendiente", "completada", "cancelada"



    public LocalDate getFechaActualLaPaz() {
        return ZonedDateTime.now(ZoneId.of("America/La_Paz")).toLocalDate();
    }

    public LocalTime getHoraActualLaPaz() {
        return ZonedDateTime.now(ZoneId.of("America/La_Paz")).toLocalTime().truncatedTo(java.time.temporal.ChronoUnit.SECONDS);
    }

    // Getters y Setters
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

    public Long getMedicoId() {
        return medicoId;  // Devuelve el ID del médico
    }

    public void setMedicoId(Long medicoId) {
        this.medicoId = medicoId;  // Establece el ID del médico
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }

    public BloqueHorario getBloqueHorario() {
        return bloqueHorario;
    }

    public void setBloqueHorario(BloqueHorario bloqueHorario) {
        this.bloqueHorario = bloqueHorario;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

}
