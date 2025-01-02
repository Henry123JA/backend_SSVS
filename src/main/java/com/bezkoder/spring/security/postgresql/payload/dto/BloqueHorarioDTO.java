package com.bezkoder.spring.security.postgresql.payload.dto;

import com.bezkoder.spring.security.postgresql.models.BloqueHorario;
import com.bezkoder.spring.security.postgresql.models.Horario;

import java.time.LocalTime;

public class BloqueHorarioDTO {

    private Long id;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private int duracion;
    private String estado;
    private Horario horario; // Aquí incluimos el horario asociado

    // Constructor, getters y setters
    public BloqueHorarioDTO(BloqueHorario bloqueHorario) {
        this.id = bloqueHorario.getId();
        this.horaInicio = bloqueHorario.getHoraInicio();
        this.horaFin = bloqueHorario.getHoraFin();
        this.duracion = bloqueHorario.getDuracion();
        this.estado = bloqueHorario.getEstado();
        this.horario = bloqueHorario.getHorario(); // Asignamos el horario
    }

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
}
