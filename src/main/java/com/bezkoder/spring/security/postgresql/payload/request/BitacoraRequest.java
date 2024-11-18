package com.bezkoder.spring.security.postgresql.payload.request;

import jakarta.validation.constraints.NotBlank;

public class BitacoraRequest {

    @NotBlank
    private String tipoAccion;  // Tipo de acción: 'CREAR', 'EDITAR', 'VER', etc.

    @NotBlank
    private String descripcionAccion;  // Descripción de la acción realizada

    @NotBlank
    private String ip;  // Dirección IP desde donde se realiza la acción

    @NotBlank
    private Long userId;  // ID del usuario al que se le asociará la bitácora

    // Getters and Setters
    public String getTipoAccion() {
        return tipoAccion;
    }

    public void setTipoAccion(String tipoAccion) {
        this.tipoAccion = tipoAccion;
    }

    public String getDescripcionAccion() {
        return descripcionAccion;
    }

    public void setDescripcionAccion(String descripcionAccion) {
        this.descripcionAccion = descripcionAccion;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

