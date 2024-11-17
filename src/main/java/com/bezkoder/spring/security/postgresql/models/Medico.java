package com.bezkoder.spring.security.postgresql.models;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "medicos")
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ci;
    private String sexo;
    private String telefono;

    @ManyToOne(fetch = FetchType.EAGER/*, cascade = CascadeType.ALL*/)
    @JoinColumn(name = "user_id")  // Clave foránea hacia User
    private User user;  // Relación con el Usuario (clave foránea)

    @ManyToMany
    @JoinTable(
            name = "medico_especialidad",
            joinColumns = @JoinColumn(name = "medico_id"),
            inverseJoinColumns = @JoinColumn(name = "especialidad_id")
    )
    private Set<Especialidad> especialidades = new HashSet<>();

    public Medico (){

    }

    // Constructor
    public Medico(String ci, String sexo, String telefono, User user) {
        this.ci = ci;
        this.sexo = sexo;
        this.telefono = telefono;
        this.user = user;
    }

    // Getters y setters
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public Set<Especialidad> getEspecialidades() {
        return especialidades;
    }

    public void setEspecialidades(Set<Especialidad> especialidades) {
        this.especialidades = especialidades;
    }

    public void addEspecialidad(Especialidad especialidad) {
        this.especialidades.add(especialidad);
    }

    public void removeEspecialidad(Especialidad especialidad) {
        this.especialidades.remove(especialidad);
    }

    @Override
    public String toString() {
        return "Medico{" +
                "id=" + id +
                ", ci='" + ci + '\'' +
                ", sexo='" + sexo + '\'' +
                ", telefono='" + telefono + '\'' +
                '}';
    }
}

