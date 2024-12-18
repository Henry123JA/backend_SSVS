
package com.bezkoder.spring.security.postgresql.models;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pacientes")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ci;

    @Column(length = 1)
    private String sexo;

    private String telefono;
    private String fechaNacimiento;  // Usamos LocalDate para la fecha de nacimiento
    private String codigoAsegurado;     // Código asegurado

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")  // Clave foránea hacia User
    private User user;  // Relación con el Usuario (clave foránea)

    public Paciente() {
        // Constructor vacío
    }

    // Constructor con los parámetros mencionados
    public Paciente(String ci, String sexo, String telefono, String fechaNacimiento, String codigoAsegurado, User user) {
        this.ci = ci;
        this.sexo = sexo;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
        this.codigoAsegurado = codigoAsegurado;
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
        if (sexo != null && sexo.length() == 1) {
            this.sexo = sexo;
        }
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Paciente{" +
                "id=" + id +
                ", ci='" + ci + '\'' +
                ", sexo='" + sexo + '\'' +
                ", telefono='" + telefono + '\'' +
                ", fechaNacimiento=" + fechaNacimiento +
                ", codigoAsegurado='" + codigoAsegurado + '\'' +
                '}';
    }
}

