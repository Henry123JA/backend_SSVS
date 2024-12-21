package com.bezkoder.spring.security.postgresql.models;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "horarios")
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalTime horaInicio;
    private LocalTime horaFin;
    private int duracion; // Duración en minutos
    private String diaSemana; // Ej: 'Lunes', 'Martes', etc.
    private String estado; // Ej: 'activo', 'inactivo'

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medico_id")  // Clave foránea hacia Medico
    private Medico medico;  // Relación con el Médico

    // Constructor vacío
    public Horario() {
    }

    // Constructor con parámetros
    public Horario(LocalTime horaInicio, LocalTime horaFin, int duracion, String diaSemana, String estado, Medico medico) {
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.duracion = duracion;
        this.diaSemana = diaSemana;
        this.estado = estado;
        this.medico = medico;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    @Override
    public String toString() {
        return "Horario{" +
                "id=" + id +
                ", horaInicio=" + horaInicio +
                ", horaFin=" + horaFin +
                ", duracion=" + duracion +
                ", diaSemana='" + diaSemana + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}

