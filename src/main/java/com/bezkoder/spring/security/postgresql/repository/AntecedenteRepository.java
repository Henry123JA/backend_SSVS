
package com.bezkoder.spring.security.postgresql.repository;

import com.bezkoder.spring.security.postgresql.models.Antecedente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AntecedenteRepository extends JpaRepository<Antecedente, Long> {
    // Método para obtener un antecedente por el ID del paciente
    Antecedente findByPacienteId(Long pacienteId);
}
