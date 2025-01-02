
package com.bezkoder.spring.security.postgresql.repository;

import com.bezkoder.spring.security.postgresql.models.BloqueHorario;
import com.bezkoder.spring.security.postgresql.models.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BloqueHorarioRepository extends JpaRepository<BloqueHorario, Long> {
    // Puedes agregar métodos personalizados si es necesario
    //List<BloqueHorario> findByMedicoIdAndDiaSemana(String diaSemana, Long medicoId);
    List<BloqueHorario> findByHorario(Horario horario);
}

