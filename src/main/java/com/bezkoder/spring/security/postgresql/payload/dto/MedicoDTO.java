
package com.bezkoder.spring.security.postgresql.payload.dto;

import com.bezkoder.spring.security.postgresql.models.Medico;

public class MedicoDTO {
    private Long id;
    private String ci;
    private String sexo;
    private String telefono;
    private UserDTO user;

    // Constructor que acepta un objeto Medico
    public MedicoDTO(Medico medico) {
        this.id = medico.getId();
        this.ci = medico.getCi();
        this.sexo = medico.getSexo();
        this.telefono = medico.getTelefono();
        this.user = new UserDTO(medico.getUser().getId(), medico.getUser().getUsername(),
                medico.getUser().getEmail(), medico.getUser().getPassword());
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

