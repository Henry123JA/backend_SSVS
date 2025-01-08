package com.bezkoder.spring.security.postgresql.payload.request;

public class CreateRecetaRequest {

    private Long consultaId;  // ID de la consulta asociada
    private String medicamentos;
    private String indicaciones;

    // Getters y Setters
    public Long getConsultaId() {
        return consultaId;
    }

    public void setConsultaId(Long consultaId) {
        this.consultaId = consultaId;
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
}
