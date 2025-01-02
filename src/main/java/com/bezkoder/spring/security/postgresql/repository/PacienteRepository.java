package com.bezkoder.spring.security.postgresql.repository;

import com.bezkoder.spring.security.postgresql.models.Paciente;
import com.bezkoder.spring.security.postgresql.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    // Método para encontrar un paciente por su usuario
    Optional<Paciente> findByUser(User user);

    Boolean existsBycodigoAsegurado(String codigoAsegurado);

    Paciente findByUserId(Long userId);

    List<Paciente> getPacientesByUserId (Long userId);

}

