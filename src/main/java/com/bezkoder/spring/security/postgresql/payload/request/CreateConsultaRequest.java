package com.bezkoder.spring.security.postgresql.payload.request;

import jakarta.validation.constraints.NotBlank;

public class CreateConsultaRequest {

    @NotBlank
    private Long fichaId;  // Relación con FichaNueva (solo el ID)

    @NotBlank
    private String sintomas;

    @NotBlank
    private String diagnostico;

    @NotBlank
    private String tratamiento;

    private String notas;

    @NotBlank
    private String fechaConsulta;  // Fecha de la consulta en formato ISO 8601

    // Getters y Setters
    public Long getFichaId() {
        return fichaId;
    }

    public void setFichaId(Long fichaId) {
        this.fichaId = fichaId;
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

    public String getFechaConsulta() {
        return fechaConsulta;
    }

    public void setFechaConsulta(String fechaConsulta) {
        this.fechaConsulta = fechaConsulta;
    }
}
