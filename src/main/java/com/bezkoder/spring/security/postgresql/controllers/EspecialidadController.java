
// src/main/java/com/bezkoder/spring/security/postgresql/controllers/EspecialidadController.java

package com.bezkoder.spring.security.postgresql.controllers;

import com.bezkoder.spring.security.postgresql.models.Especialidad;
import com.bezkoder.spring.security.postgresql.repository.EspecialidadRepository;
import com.bezkoder.spring.security.postgresql.exceptions.ResourceNotFoundException;
import com.bezkoder.spring.security.postgresql.payload.response.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

        import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/especialidades")
public class EspecialidadController {

    @Autowired
    EspecialidadRepository especialidadRepository;

    // Obtener todas las especialidades
    @GetMapping
    public List<Especialidad> getAllEspecialidades() {
        return especialidadRepository.findAll();
    }

    // Crear una nueva especialidad
    @PostMapping
    public ResponseEntity<Especialidad> createEspecialidad(@RequestBody Especialidad especialidad) {
        Especialidad newEspecialidad = especialidadRepository.save(especialidad);
        return new ResponseEntity<>(newEspecialidad, HttpStatus.CREATED);
    }

    // Obtener especialidad por ID
    @GetMapping("/{id}")
    public ResponseEntity<Especialidad> getEspecialidadById(@PathVariable Long id) {
        Especialidad especialidad = especialidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada con id " + id));
        return ResponseEntity.ok(especialidad);
    }

    // Actualizar especialidad
    @PutMapping("/{id}")
    public ResponseEntity<Especialidad> updateEspecialidad(@PathVariable Long id, @RequestBody Especialidad especialidadDetails) {
        Especialidad especialidad = especialidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada con id " + id));

        especialidad.setNombre(especialidadDetails.getNombre());
        especialidad.setDescripcion(especialidadDetails.getDescripcion());

        final Especialidad updatedEspecialidad = especialidadRepository.save(especialidad);
        return ResponseEntity.ok(updatedEspecialidad);
    }

    // Eliminar especialidad
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteEspecialidad(@PathVariable Long id) {
        Especialidad especialidad = especialidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada con id " + id));

        especialidadRepository.delete(especialidad);
        return ResponseEntity.ok(new MessageResponse("Especialidad eliminada con éxito"));
    }
}

