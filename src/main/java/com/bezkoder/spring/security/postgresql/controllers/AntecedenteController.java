
package com.bezkoder.spring.security.postgresql.controllers;

import com.bezkoder.spring.security.postgresql.models.Antecedente;
import com.bezkoder.spring.security.postgresql.models.Paciente;
import com.bezkoder.spring.security.postgresql.exceptions.ResourceNotFoundException;
import com.bezkoder.spring.security.postgresql.repository.AntecedenteRepository;
import com.bezkoder.spring.security.postgresql.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:4200", "*"})
@RestController
@RequestMapping("/api/antecedentes")
public class AntecedenteController {

    @Autowired
    private AntecedenteRepository antecedenteRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    // Obtener todos los antecedentes
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Antecedente>> getAllAntecedentes() {
        List<Antecedente> antecedentes = antecedenteRepository.findAll();
        return ResponseEntity.ok(antecedentes);
    }

    // Obtener los antecedentes de un user por su ID
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('PACIENTE') or hasRole('MEDICO') or hasRole('ADMIN')")
    public ResponseEntity<Antecedente> getAntecedenteByPaciente(@PathVariable Long userId) {
        Paciente paciente = pacienteRepository.findByUserId(userId);

        if (paciente == null) {
            return ResponseEntity.notFound().build(); // Paciente no encontrado
        }

        // Obtener el antecedente del paciente
        Antecedente antecedente = antecedenteRepository.findByPacienteId(paciente.getId());

        if (antecedente == null) {
            return ResponseEntity.noContent().build(); // No hay antecedentes
        }

        return ResponseEntity.ok(antecedente); // Antecedente encontrado
    }


    // Crear un nuevo antecedente
    @PostMapping
    @PreAuthorize("hasRole('MEDICO') or hasRole('ADMIN')")
    public ResponseEntity<Antecedente> createAntecedente(@RequestBody Antecedente antecedenteRequest) {
        Paciente paciente = pacienteRepository.findById(antecedenteRequest.getPaciente().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));

        // Verificar si el paciente ya tiene antecedentes
        Antecedente existingAntecedente = antecedenteRepository.findByPacienteId(paciente.getId());
        if (existingAntecedente != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Antecedente()); // El paciente ya tiene antecedentes
        }

        // Asignar el paciente y crear el nuevo antecedente
        antecedenteRequest.setPaciente(paciente);
        Antecedente newAntecedente = antecedenteRepository.save(antecedenteRequest);

        // Devolver el antecedente creado
        return new ResponseEntity<>(newAntecedente, HttpStatus.CREATED);
    }

    // Obtener un antecedente por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MEDICO') or hasRole('PACIENTE') or hasRole('ADMIN')")
    public ResponseEntity<Antecedente> getAntecedenteById(@PathVariable Long id) {
        Antecedente antecedente = antecedenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Antecedente no encontrado con el id " + id));
        return ResponseEntity.ok(antecedente);
    }

    // Actualizar un antecedente existente
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MEDICO') or hasRole('ADMIN')")
    public ResponseEntity<Antecedente> updateAntecedente(@PathVariable Long id, @RequestBody Antecedente antecedenteRequest) {
        // Verificar si el antecedente existe
        Antecedente existingAntecedente = antecedenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Antecedente no encontrado"));

        // Verificar si el paciente existe
        Paciente paciente = pacienteRepository.findById(antecedenteRequest.getPaciente().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));

        // Actualizar los datos del antecedente con los nuevos valores
        existingAntecedente.setEnfermedadesBase(antecedenteRequest.getEnfermedadesBase());
        existingAntecedente.setEnfermedadesHereditarias(antecedenteRequest.getEnfermedadesHereditarias());
        existingAntecedente.setOtras(antecedenteRequest.getOtras());
        existingAntecedente.setFecha(antecedenteRequest.getFecha());
        existingAntecedente.setPaciente(paciente);

        // Guardar los cambios en la base de datos
        Antecedente updatedAntecedente = antecedenteRepository.save(existingAntecedente);

        // Devolver el antecedente actualizado
        return ResponseEntity.ok(updatedAntecedente);
    }


    // Eliminar un antecedente
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAntecedente(@PathVariable Long id) {
        Antecedente antecedente = antecedenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Antecedente no encontrado con el id " + id));
        antecedenteRepository.delete(antecedente);
        return ResponseEntity.noContent().build();
    }
}
