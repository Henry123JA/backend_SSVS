
package com.bezkoder.spring.security.postgresql.controllers;

import com.bezkoder.spring.security.postgresql.models.Horario;
import com.bezkoder.spring.security.postgresql.models.Medico;
import com.bezkoder.spring.security.postgresql.repository.HorarioRepository;
import com.bezkoder.spring.security.postgresql.repository.MedicoRepository;
import com.bezkoder.spring.security.postgresql.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:4200", "*"})
@RestController
@RequestMapping("/api/horarios")
public class HorarioController {

    @Autowired
    private HorarioRepository horarioRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    // Obtener todos los horarios
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Horario>> getAllHorarios() {
        //System.out.println("ingreso a listar horarios: ");
        List<Horario> horarios = horarioRepository.findAll();
        return ResponseEntity.ok(horarios);
    }

    // Crear un nuevo horario
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Horario> createHorario(@RequestBody Horario horarioRequest) {
        //System.out.println("ingreso a crear horarios: ");
        Medico medico = medicoRepository.findById(horarioRequest.getMedico().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Medico no encontrado"));
        //System.out.println("usarname del medico elegido: "+medico.getUser().getUsername());
        horarioRequest.setMedico(medico);
        Horario newHorario = horarioRepository.save(horarioRequest);
        //System.out.println(" creo y guardo horarios: ");
        return new ResponseEntity<>(newHorario, HttpStatus.CREATED);
    }

    // Obtener un horario por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Horario> getHorarioById(@PathVariable Long id) {
        Horario horario = horarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horario no encontrado con el id " + id));

        return ResponseEntity.ok(horario);
    }

    // Actualizar un horario
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Horario> updateHorario(@PathVariable Long id, @RequestBody Horario horarioRequest) {
        Horario existingHorario = horarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horario no encontrado con el id " + id));

        existingHorario.setHoraInicio(horarioRequest.getHoraInicio());
        existingHorario.setHoraFin(horarioRequest.getHoraFin());
        existingHorario.setDuracion(horarioRequest.getDuracion());
        existingHorario.setDiaSemana(horarioRequest.getDiaSemana());
        existingHorario.setEstado(horarioRequest.getEstado());

        Horario updatedHorario = horarioRepository.save(existingHorario);
        return ResponseEntity.ok(updatedHorario);
    }

    // Eliminar un horario
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteHorario(@PathVariable Long id) {
        Horario horario = horarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horario no encontrado con el id " + id));

        horarioRepository.delete(horario);
        return ResponseEntity.noContent().build();
    }
}

