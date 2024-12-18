package com.bezkoder.spring.security.postgresql.payload.request;

import com.bezkoder.spring.security.postgresql.models.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreatePacienteRequest {

    private User user;

    @NotBlank
    private String ci; // Cédula de Identidad

    @NotBlank
    private String sexo; // Sexo

    @NotBlank
    private String telefono; // Teléfono

    @NotBlank
    private String fechaNacimiento; // Fecha de nacimiento

    @NotBlank
    @Size(min = 4, max = 10)
    private String codigoAsegurado; // Código Asegurado

    // Getters y Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCi() {
        return ci;
    }

    public void setCi(String ci) {
        this.ci = ci;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getCodigoAsegurado() {
        return codigoAsegurado;
    }

    public void setCodigoAsegurado(String codigoAsegurado) {
        this.codigoAsegurado = codigoAsegurado;
    }
}

