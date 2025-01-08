
package com.bezkoder.spring.security.postgresql.repository;

import com.bezkoder.spring.security.postgresql.models.Consulta;
import com.bezkoder.spring.security.postgresql.models.Receta;
import com.bezkoder.spring.security.postgresql.models.Tratamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TratamientoRepository extends JpaRepository<Tratamiento, Long> {

    // Método para encontrar tratamientos por consulta
    Optional<Tratamiento> findByConsultaId(Long consultaId);
//findByConsultaIdIn
    // Método para verificar si ya existe un tratamiento para una consulta dada
    boolean existsByConsultaId(Long consultaId);

    // Método para encontrar tratamientos por el seguimiento
    List<Tratamiento> findBySeguimiento(String seguimiento);

    List<Tratamiento> findByConsultaIn(List<Consulta> consultas);

    // Método para encontrar tratamiento por su id
    Optional<Tratamiento> findById(Long id);
}
