package com.bezkoder.spring.security.postgresql.repository;

import com.bezkoder.spring.security.postgresql.models.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    // Método para encontrar una consulta por su ficha asociada
    List<Consulta> findByFichaId(Long fichaId);

    // Método para verificar si ya existe una consulta para una ficha dada
    boolean existsByFichaId(Long fichaId);

    List<Consulta> findByFichaMedicoId(Long medicoId);

}
