
package com.bezkoder.spring.security.postgresql.repository;

import com.bezkoder.spring.security.postgresql.models.MedicoEspecialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicoEspecialidadRepository extends JpaRepository<MedicoEspecialidad, Long> {
    List<MedicoEspecialidad> findAll();
}

