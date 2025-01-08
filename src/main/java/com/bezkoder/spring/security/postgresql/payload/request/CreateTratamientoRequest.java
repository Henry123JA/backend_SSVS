
package com.bezkoder.spring.security.postgresql.payload.request;

import java.util.Date;

public class CreateTratamientoRequest {

    private Long consultaId;  // ID de la consulta asociada
    private String descripcion;  // Descripción del tratamiento
    private Date fechaInicio;  // Fecha de inicio del tratamiento
    private Date fechaFin;  // Fecha de fin del tratamiento
    private String seguimiento;  // Seguimiento del tratamiento

    // Getters y Setters
    public Long getConsultaId() {
        return consultaId;
    }

    public void setConsultaId(Long consultaId) {
        this.consultaId = consultaId;
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
