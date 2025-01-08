package com.bezkoder.spring.security.postgresql.controllers;

import com.bezkoder.spring.security.postgresql.models.FichaNueva;
import com.bezkoder.spring.security.postgresql.models.Medico;
import com.bezkoder.spring.security.postgresql.models.Paciente;
import com.bezkoder.spring.security.postgresql.models.Especialidad;
import com.bezkoder.spring.security.postgresql.models.BloqueHorario;
import com.bezkoder.spring.security.postgresql.exceptions.ResourceNotFoundException;
import com.bezkoder.spring.security.postgresql.repository.FichaNuevaRepository;
import com.bezkoder.spring.security.postgresql.repository.MedicoRepository;
import com.bezkoder.spring.security.postgresql.repository.PacienteRepository;
import com.bezkoder.spring.security.postgresql.repository.EspecialidadRepository;
import com.bezkoder.spring.security.postgresql.repository.BloqueHorarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:4200", "*"})
@RestController
@RequestMapping("/api/fichas")
public class FichaNuevaController {

    @Autowired
    private FichaNuevaRepository fichaNuevaRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private EspecialidadRepository especialidadRepository;

    @Autowired
    private BloqueHorarioRepository bloqueHorarioRepository;

    // Obtener todas las fichas
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FichaNueva>> getAllFichas() {
        List<FichaNueva> fichas = fichaNuevaRepository.findAll();
        return ResponseEntity.ok(fichas);
    }

    // Obtener las fichas de un paciente por su ID----------------------
    @GetMapping("/paciente/{userId}")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<List<FichaNueva>> getFichasByPaciente(@PathVariable Long userId) {
        Paciente paciente = pacienteRepository.findByUserId(userId);

        if (paciente == null) {
            return ResponseEntity.notFound().build();
        }
        List<FichaNueva> fichas = fichaNuevaRepository.findByPacienteId(paciente.getId());
        return ResponseEntity.ok(fichas);
    }//hasta aqui....

    // Obtener las fichas de un paciente por su ID
    @GetMapping("/medico/{userId}")
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<List<FichaNueva>> getFichasByMedico(@PathVariable Long userId) {
        Medico medico = medicoRepository.findByUserId(userId);

        if (medico == null) {
            return ResponseEntity.notFound().build();
        }
        List<FichaNueva> fichas = fichaNuevaRepository.findByMedicoId(medico.getId());
        return ResponseEntity.ok(fichas);
    }//hasta aqui....

    // Método para obtener las fichas filtradas por especialidad
    @GetMapping("/especialidad/{especialidadId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FichaNueva>> getFichasByEspecialidad(@PathVariable Long especialidadId) {
        List<FichaNueva> fichas = fichaNuevaRepository.findByEspecialidadId(especialidadId);

        if (fichas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(fichas);
    }

    // Crear una nueva ficha
    @PostMapping
    @PreAuthorize("hasRole('PACIENTE') or hasRole('ADMIN')")
    public ResponseEntity<FichaNueva> createFicha(@RequestBody FichaNueva fichaRequest) {
        Paciente paciente = pacienteRepository.findById(fichaRequest.getPaciente().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));

        // Buscar al médico asociado
        Medico medico = medicoRepository.findById(fichaRequest.getMedicoId())
                .orElseThrow(() -> new ResourceNotFoundException("Médico no encontrado"));

        // Buscar la especialidad asociada
        Especialidad especialidad = especialidadRepository.findById(fichaRequest.getEspecialidad().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada"));

        // Buscar el bloque horario asociado
        BloqueHorario bloqueHorario = bloqueHorarioRepository.findById(fichaRequest.getBloqueHorario().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Bloque de horario no encontrado"));
        bloqueHorario.setEstado("ocupado");
        BloqueHorario bloqueHorario2 = bloqueHorarioRepository.save(bloqueHorario);

        // Asignar las relaciones a la ficha
        fichaRequest.setPaciente(paciente);
        fichaRequest.setMedicoId(medico.getId());
        fichaRequest.setEspecialidad(especialidad);
        fichaRequest.setBloqueHorario(bloqueHorario2);
        fichaRequest.setFecha(fichaRequest.getFechaActualLaPaz());
        fichaRequest.setHora(fichaRequest.getHoraActualLaPaz());
        FichaNueva newFicha = fichaNuevaRepository.save(fichaRequest);
        // Devolver la ficha creada
        return new ResponseEntity<>(newFicha,HttpStatus.CREATED);
    }

    // Obtener una ficha por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PACIENTE') or hasRole('MEDICO')")
    public ResponseEntity<FichaNueva> getFichaById(@PathVariable Long id) {
        FichaNueva ficha = fichaNuevaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ficha no encontrada con el id " + id));
        return ResponseEntity.ok(ficha);
    }
/*
    // Actualizar una ficha
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FichaNueva> updateFicha(@PathVariable Long id, @RequestBody FichaNueva fichaRequest) {
        FichaNueva existingFicha = fichaNuevaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ficha no encontrada con el id " + id));

        existingFicha.setPaciente(fichaRequest.getPaciente());
        existingFicha.setMedicoId(fichaRequest.getMedicoId());
        existingFicha.setEspecialidad(fichaRequest.getEspecialidad());
        existingFicha.setBloqueHorario(fichaRequest.getBloqueHorario());
        existingFicha.setFecha(fichaRequest.getFecha());
        existingFicha.setEstado(fichaRequest.getEstado());

        FichaNueva updatedFicha = fichaNuevaRepository.save(existingFicha);
        return ResponseEntity.ok(updatedFicha);
    }
*/
    // Eliminar una ficha
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFicha(@PathVariable Long id) {
        FichaNueva ficha = fichaNuevaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ficha no encontrada con el id " + id));
        fichaNuevaRepository.delete(ficha);
        return ResponseEntity.noContent().build();
    }
}
