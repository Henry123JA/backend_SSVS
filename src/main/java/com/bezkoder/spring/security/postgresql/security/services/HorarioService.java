package com.bezkoder.spring.security.postgresql.security.services;

import com.bezkoder.spring.security.postgresql.models.BloqueHorario;
import com.bezkoder.spring.security.postgresql.models.Horario;
import com.bezkoder.spring.security.postgresql.repository.BloqueHorarioRepository;
import com.bezkoder.spring.security.postgresql.repository.HorarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class HorarioService {

    @Autowired
    private HorarioRepository horarioRepository;

    @Autowired
    private BloqueHorarioRepository bloqueHorarioRepository;

    public void crearHorariosConBloques(Horario horario) {
        // Obtener horaInicio, horaFin y duración
        LocalTime horaInicio = horario.getHoraInicio();
        LocalTime horaFin = horario.getHoraFin();
        int duracion = horario.getDuracion();
        String estado = "libre";

        // Crear los bloques de tiempo
        List<BloqueHorario> bloques = new ArrayList<>();

        // Mientras horaInicio sea menor o igual que horaFin
        while (!horaInicio.plusMinutes(duracion).isAfter(horaFin)) {
            // Calcular el tiempo de fin del bloque
            LocalTime horaFinBloque = horaInicio.plusMinutes(duracion);

            // Crear el bloque de tiempo
            BloqueHorario bloque = new BloqueHorario(horaInicio, horaFinBloque, duracion, "libre",horario);
            bloques.add(bloque);

            // Avanzar al siguiente bloque
            horaInicio = horaFinBloque;
        }

        // Guardar los bloques en la base de datos
        bloqueHorarioRepository.saveAll(bloques);
    }

    // Obtener todos los bloques de horario asociados a un horario específico
    public List<BloqueHorario> getBloquesByHorario(Horario horario) {
        return bloqueHorarioRepository.findByHorario(horario);
    }

    // Obtener un bloque de horario por ID
    public Optional<BloqueHorario> getBloqueById(Long id) {
        return bloqueHorarioRepository.findById(id);
    }

    // Eliminar un bloque de horario
    public void deleteBloque(BloqueHorario bloque) {
        bloqueHorarioRepository.delete(bloque);
    }
}
