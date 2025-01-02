package com.bezkoder.spring.security.postgresql.repository;

import com.bezkoder.spring.security.postgresql.models.FichaNueva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FichaNuevaRepository extends JpaRepository<FichaNueva, Long> {
    List<FichaNueva> findByPacienteId(Long pacienteId);
    List<FichaNueva> findByMedicoId(Long medicoId);

}
