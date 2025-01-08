
package com.bezkoder.spring.security.postgresql.controllers;

import com.bezkoder.spring.security.postgresql.models.Consulta;
import com.bezkoder.spring.security.postgresql.models.Tratamiento;
import com.bezkoder.spring.security.postgresql.models.Medico;
import com.bezkoder.spring.security.postgresql.payload.request.CreateTratamientoRequest;
import com.bezkoder.spring.security.postgresql.repository.ConsultaRepository;
import com.bezkoder.spring.security.postgresql.repository.TratamientoRepository;
import com.bezkoder.spring.security.postgresql.repository.MedicoRepository;
import com.bezkoder.spring.security.postgresql.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:4200", "*"})
@RestController
@RequestMapping("/api/tratamientos")
public class TratamientoController {

    @Autowired
    private TratamientoRepository tratamientoRepository;

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    // Obtener todos los tratamientos
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Object>> getAllTratamientos() {
        List<Tratamiento> tratamientos = tratamientoRepository.findAll();

        // Mapeo de tratamientos a un formato simplificado
        List<Object> tratamientoResponse = tratamientos.stream().map(tratamiento -> {
            return Map.of(
                    "id", tratamiento.getId(),
                    "consultaId", tratamiento.getConsulta().getId(),
                    "descripcion", tratamiento.getDescripcion(),
                    "fechaInicio", tratamiento.getFechaInicio(),
                    "fechaFin", tratamiento.getFechaFin(),
                    "seguimiento", tratamiento.getSeguimiento()
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(tratamientoResponse);
    }

    // Obtener un tratamiento por su ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MEDICO') or hasRole('PACIENTE') or hasRole('ADMIN')")
    public ResponseEntity<Object> getTratamientoById(@PathVariable Long id) {
        Tratamiento tratamiento = tratamientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tratamiento no encontrado con el id " + id));

        // Mapeo simplificado del tratamiento
        Map<String, Object> tratamientoResponse = Map.of(
                "id", tratamiento.getId(),
                "consultaId", tratamiento.getConsulta().getId(),
                "descripcion", tratamiento.getDescripcion(),
                "fechaInicio", tratamiento.getFechaInicio(),
                "fechaFin", tratamiento.getFechaFin(),
                "seguimiento", tratamiento.getSeguimiento()
        );

        return ResponseEntity.ok(tratamientoResponse);
    }

    // Crear un nuevo tratamiento
    @PostMapping
    @PreAuthorize("hasRole('MEDICO') or hasRole('ADMIN')")
    public ResponseEntity<Tratamiento> createTratamiento(@RequestBody CreateTratamientoRequest request) {
        // Buscar la consulta correspondiente
        Consulta consulta = consultaRepository.findById(request.getConsultaId())
                .orElseThrow(() -> new ResourceNotFoundException("Consulta no encontrada con el id " + request.getConsultaId()));

        // Crear un nuevo tratamiento
        Tratamiento tratamiento = new Tratamiento();
        tratamiento.setDescripcion(request.getDescripcion());
        tratamiento.setFechaInicio(request.getFechaInicio());
        tratamiento.setFechaFin(request.getFechaFin());
        tratamiento.setSeguimiento(request.getSeguimiento());
        tratamiento.setConsulta(consulta);

        // Guardar el tratamiento en la base de datos
        Tratamiento savedTratamiento = tratamientoRepository.save(tratamiento);

        System.out.println("llego y guardo el tratamiento...");
        return ResponseEntity.ok(savedTratamiento);
    }

    // Actualizar un tratamiento existente
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MEDICO') or hasRole('ADMIN')")
    public ResponseEntity<Tratamiento> updateTratamiento(@PathVariable Long id, @RequestBody Tratamiento tratamientoRequest) {
        // Verificar si el tratamiento existe
        Tratamiento existingTratamiento = tratamientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tratamiento no encontrado con el id " + id));

        // Verificar si la consulta existe
        Consulta consulta = consultaRepository.findById(tratamientoRequest.getConsulta().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Consulta no encontrada con el id " + tratamientoRequest.getConsulta().getId()));

        // Actualizar los datos del tratamiento
        existingTratamiento.setDescripcion(tratamientoRequest.getDescripcion());
        existingTratamiento.setFechaInicio(tratamientoRequest.getFechaInicio());
        existingTratamiento.setFechaFin(tratamientoRequest.getFechaFin());
        existingTratamiento.setSeguimiento(tratamientoRequest.getSeguimiento());
        existingTratamiento.setConsulta(consulta);

        // Guardar los cambios en la base de datos
        Tratamiento updatedTratamiento = tratamientoRepository.save(existingTratamiento);

        return ResponseEntity.ok(updatedTratamiento);
    }

    // Eliminar un tratamiento
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MEDICO')")
    public ResponseEntity<Void> deleteTratamiento(@PathVariable Long id) {
        Tratamiento tratamiento = tratamientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tratamiento no encontrado con el id " + id));

        tratamientoRepository.delete(tratamiento);

        return ResponseEntity.noContent().build();
    }

    //-------------------Tratamientos de un medico-------------------
    // Obtener los tratamientos por medicoId
    @GetMapping("/medico/{userId}")
    @PreAuthorize("hasRole('MEDICO') or hasRole('ADMIN')")
    public ResponseEntity<List<Object>> getTratamientosByMedico(@PathVariable Long userId) {
        // Buscar el médico a partir del userId
        Medico medic = medicoRepository.findByUserId(userId);

        if (medic == null) {
            return ResponseEntity.notFound().build();  // Devuelve no encontrado si el médico no existe
        }

        // Obtener las consultas del médico para encontrar los tratamientos asociados
        List<Consulta> consultas = consultaRepository.findByFichaMedicoId(medic.getId());

        if (consultas.isEmpty()) {
            return ResponseEntity.noContent().build();  // Si no hay consultas, devuelve no content
        }

        // Obtener los tratamientos asociados a esas consultas
        List<Tratamiento> tratamientos = tratamientoRepository.findByConsultaIn(consultas);

        if (tratamientos.isEmpty()) {
            return ResponseEntity.noContent().build();  // Si no hay tratamientos, devuelve no content
        }

        // Mapeo de tratamientos a un formato simplificado
        List<Object> tratamientoResponse = tratamientos.stream().map(tratamiento -> {
            return Map.of(
                    "id", tratamiento.getId(),
                    "consultaId", tratamiento.getConsulta().getId(),
                    "descripcion", tratamiento.getDescripcion(),
                    "fechaInicio", tratamiento.getFechaInicio(),
                    "fechaFin", tratamiento.getFechaFin(),
                    "seguimiento", tratamiento.getSeguimiento()
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(tratamientoResponse);
    }

    // Obtener los tratamientos asociados a una consulta
    @GetMapping("/consulta/{consultaId}")
    @PreAuthorize("hasRole('MEDICO') or hasRole('PACIENTE') or hasRole('ADMIN')")
    public ResponseEntity<Object> getTratamientosByConsulta(@PathVariable Long consultaId) {
        Tratamiento tratamiento = tratamientoRepository.findByConsultaId(consultaId)
                .orElseThrow(() -> new ResourceNotFoundException("Tratamiento no encontrado para la consulta con id " + consultaId));

        // Mapeo simplificado del tratamiento
        Map<String, Object> tratamientoResponse = Map.of(
                "id", tratamiento.getId(),
                "consultaId", tratamiento.getConsulta().getId(),
                "descripcion", tratamiento.getDescripcion(),
                "fechaInicio", tratamiento.getFechaInicio(),
                "fechaFin", tratamiento.getFechaFin(),
                "seguimiento", tratamiento.getSeguimiento()
        );

        return ResponseEntity.ok(tratamientoResponse);
    }

}
