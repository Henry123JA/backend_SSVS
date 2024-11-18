package com.bezkoder.spring.security.postgresql.repository;

import com.bezkoder.spring.security.postgresql.models.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    // Buscar un paciente por su cédula de identidad
    Optional<Paciente> findByCi(String ci);

    // Buscar un paciente por su ID de usuario
    Optional<Paciente> findByUserId(Long userId);

    // Puedes agregar otros métodos personalizados si los necesitas
}
