
package com.bezkoder.spring.security.postgresql.repository;

import com.bezkoder.spring.security.postgresql.models.Consulta;
import com.bezkoder.spring.security.postgresql.models.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, Long> {

    // Método para buscar una receta por su consulta asociada
    Optional<Receta> findByConsultaId(Long consultaId);

    // Método para verificar si ya existe una receta para una consulta dada
    boolean existsByConsultaId(Long consultaId);

    List<Receta> findByConsultaIn(List<Consulta> consultas);
}
