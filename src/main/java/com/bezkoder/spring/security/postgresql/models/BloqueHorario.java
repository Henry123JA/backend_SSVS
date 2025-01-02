package com.bezkoder.spring.security.postgresql.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "bloques_horarios")
public class BloqueHorario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalTime horaInicio;
    private LocalTime horaFin;
    private int duracion; // Duración del bloque, será la misma para todos (15 minutos, por ejemplo)
    private String estado; // Estado del bloque (activo, inactivo)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "horario_id")  // Clave foránea hacia Horario
    @JsonIgnore
    private Horario horario;  // Relación con el Horario

    // Constructor vacío
    public BloqueHorario() {
    }

    // Constructor con parámetros
    public BloqueHorario(LocalTime horaInicio, LocalTime horaFin, int duracion, String estado, Horario horario) {
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.duracion = duracion;
        this.estado = estado;
        this.horario = horario;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Horario getHorario() {
        return horario;
    }

    public void setHorario(Horario horario) {
        this.horario = horario;
    }

    @Override
    public String toString() {
        return "BloqueHorario{" +
                "id=" + id +
                ", horaInicio=" + horaInicio +
                ", horaFin=" + horaFin +
                ", duracion=" + duracion +
                ", estado='" + estado + '\'' +
                '}';
    }
}

