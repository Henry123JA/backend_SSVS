
package com.bezkoder.spring.security.postgresql.payload.request;

public class CreateExamenRequest {

    private Long consultaId;  // ID de la consulta asociada
    private String laboratorioNombre;  // Nombre del laboratorio
    private String tipoExamen;  // Tipo de examen realizado
    private String resultados;  // Resultados del examen
    private String fechaExamen;

    // Getters y Setters
    public Long getConsultaId() {
        return consultaId;
    }

    public void setConsultaId(Long consultaId) {
        this.consultaId = consultaId;
    }

    public String getLaboratorioNombre() {
        return laboratorioNombre;
    }

    public void setLaboratorioNombre(String laboratorioNombre) {
        this.laboratorioNombre = laboratorioNombre;
    }

    public String getTipoExamen() {
        return tipoExamen;
    }

    public void setTipoExamen(String tipoExamen) {
        this.tipoExamen = tipoExamen;
    }

    public String getResultados() {
        return resultados;
    }

    public void setResultados(String resultados) {
        this.resultados = resultados;
    }

    public String getFechaExamen() {
        return fechaExamen;
    }

    public void setFechaExamen(String fechaExamen) {
        this.fechaExamen = fechaExamen;
    }
}
