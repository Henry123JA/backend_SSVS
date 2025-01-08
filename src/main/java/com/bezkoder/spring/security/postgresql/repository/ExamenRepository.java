package com.bezkoder.spring.security.postgresql.repository;

import com.bezkoder.spring.security.postgresql.models.Consulta;
import com.bezkoder.spring.security.postgresql.models.Examen;
import com.bezkoder.spring.security.postgresql.models.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamenRepository extends JpaRepository<Examen, Long> {

    // Método para obtener todos los exámenes asociados a una consulta
    Optional<Examen> findByConsultaId(Long consultaId);
    List<Examen> findAllByConsultaId(Long consultaId);
    List<Examen> findByConsultaIn(List<Consulta> consultas);

    // Método para obtener un examen por su ID
    Examen findById(long id);

    // Método para obtener un examen por su ID
    Optional<Examen> findById(Long id);

    // Método para obtener todos los exámenes asociados a una consulta específica (por objeto Consulta)
    List<Examen> findByConsulta(Consulta consulta);

    // Método para obtener todos los exámenes por tipo de examen
    List<Examen> findByTipoExamen(String tipoExamen);

    // Método para obtener todos los exámenes realizados en un laboratorio específico
    List<Examen> findByLaboratorioNombre(String laboratorioNombre);
}
