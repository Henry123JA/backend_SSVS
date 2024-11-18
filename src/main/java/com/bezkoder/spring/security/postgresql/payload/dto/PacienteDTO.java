package com.bezkoder.spring.security.postgresql.payload.dto;

import com.bezkoder.spring.security.postgresql.models.Paciente;

public class PacienteDTO {
    private Long id;
    private String ci;
    private String sexo;
    private String telefono;
    private UserDTO user;

    // Constructor que acepta un objeto Paciente
    public PacienteDTO(Paciente paciente) {
        this.id = paciente.getId();
        this.ci = paciente.getCi();
        this.sexo = paciente.getSexo();
        this.telefono = paciente.getTelefono();
        this.user = new UserDTO(paciente.getUser().getId(),
                paciente.getUser().getUsername(),
                paciente.getUser().getEmail(),
                paciente.getUser().getPassword());
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
}