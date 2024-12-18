package com.bezkoder.spring.security.postgresql.models;
import com.bezkoder.spring.security.postgresql.models.User;
import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.Date;

@Entity
@Table(name = "bitacoras")
public class Bitacora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_accion")
    private String tipoAccion;  // 'CREAR', 'EDITAR', 'VER', etc.

    @Column(name = "descripcion_accion")
    private String descripcionAccion;

    @Column(name = "ip")
    private String ip;  // Dirección IP desde la que se realizó la acción

    @Column(name = "fecha_hora")
    private String  fechaHora;  // Fecha y hora de la acción

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;  // Relación con el usuario

    public Bitacora() {}

    public Bitacora(String tipoAccion, String descripcionAccion, String ip, String  fechaHora, User user) {
        this.tipoAccion = tipoAccion;
        this.descripcionAccion = descripcionAccion;
        this.ip = ip;
        this.fechaHora = fechaHora;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String  getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String  fechaHora) {
        this.fechaHora = fechaHora;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

