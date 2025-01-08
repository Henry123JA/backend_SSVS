package com.bezkoder.spring.security.postgresql.controllers;

import com.bezkoder.spring.security.postgresql.models.Medico;
import com.bezkoder.spring.security.postgresql.models.Receta;
import com.bezkoder.spring.security.postgresql.models.Consulta;
import com.bezkoder.spring.security.postgresql.repository.MedicoRepository;
import com.bezkoder.spring.security.postgresql.repository.RecetaRepository;
import com.bezkoder.spring.security.postgresql.repository.ConsultaRepository;
import com.bezkoder.spring.security.postgresql.exceptions.ResourceNotFoundException;
import com.bezkoder.spring.security.postgresql.payload.request.CreateRecetaRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import java.time.format.DateTimeFormatter;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:4200", "*"})
@RestController
@RequestMapping("/api/recetas")
public class RecetaController {

    @Autowired
    private RecetaRepository recetaRepository;

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    // Obtener todas las recetas
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Object>> getAllRecetas() {
        List<Receta> recetas = recetaRepository.findAll();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").withZone(ZoneId.of("America/La_Paz"));

        // Mapeo de recetas a un formato simplificado
        List<Object> recetaResponse = recetas.stream().map(receta -> {
            String formattedFechaConsulta = formatter.format(receta.getFechaConsulta());
            return Map.of(
                    "id", receta.getId(),
                    "consultaId", receta.getConsulta().getId(),
                    "medicamentos", receta.getMedicamentos(),
                    "indicaciones", receta.getIndicaciones(),
                    "fechaConsulta", formattedFechaConsulta
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(recetaResponse);
    }

    // Obtener una receta por su ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MEDICO') or hasRole('PACIENTE') or hasRole('ADMIN')")
    public ResponseEntity<Object> getRecetaById(@PathVariable Long id) {
        Receta receta = recetaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receta no encontrada con el id " + id));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").withZone(ZoneId.of("America/La_Paz"));
        // Mapeo simplificado de la receta
        Map<String, Object> recetaResponse = Map.of(
                "id", receta.getId(),
                "consultaId", receta.getConsulta().getId(),
                "medicamentos", receta.getMedicamentos(),
                "indicaciones", receta.getIndicaciones(),
                "fechaConsulta", formatter.format(receta.getFechaConsulta())
        );

        return ResponseEntity.ok(recetaResponse);
    }

    // Obtener la receta asociada a una consulta
    @GetMapping("/consulta/{consultaId}")
    @PreAuthorize("hasRole('MEDICO') or hasRole('PACIENTE') or hasRole('ADMIN')")
    public ResponseEntity<Object> getRecetaByConsulta(@PathVariable Long consultaId) {
        Receta receta = recetaRepository.findByConsultaId(consultaId)
                .orElseThrow(() -> new ResourceNotFoundException("Receta no encontrada para la consulta con id " + consultaId));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").withZone(ZoneId.of("America/La_Paz"));
        // Mapeo simplificado de la receta
        Map<String, Object> recetaResponse = Map.of(
                "id", receta.getId(),
                "consultaId", receta.getConsulta().getId(),
                "medicamentos", receta.getMedicamentos(),
                "indicaciones", receta.getIndicaciones(),
                "fechaConsulta", formatter.format(receta.getFechaConsulta())
        );

        return ResponseEntity.ok(recetaResponse);
    }

    // Crear una nueva receta
    @PostMapping
    @PreAuthorize("hasRole('MEDICO') or hasRole('ADMIN')")
    public ResponseEntity<Receta> createReceta(@RequestBody CreateRecetaRequest request) {
        // Buscar la consulta correspondiente al consultaId
        Consulta consulta = consultaRepository.findById(request.getConsultaId())
                .orElseThrow(() -> new ResourceNotFoundException("Consulta no encontrada con el id " + request.getConsultaId()));

        // Crear una nueva instancia de Receta
        Receta receta = new Receta();
        receta.setMedicamentos(request.getMedicamentos());
        receta.setIndicaciones(request.getIndicaciones());
        receta.setConsulta(consulta); // Asociar la receta con la consulta

        // Guardar la receta en la base de datos
        Receta savedReceta = recetaRepository.save(receta);

        // Retornar la receta guardada
        return ResponseEntity.ok(savedReceta);
    }

    // Actualizar una receta existente
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MEDICO') or hasRole('ADMIN')")
    public ResponseEntity<Receta> updateReceta(@PathVariable Long id, @RequestBody Receta recetaRequest) {
        // Verificar si la receta existe
        Receta existingReceta = recetaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receta no encontrada con el id " + id));

        // Verificar si la consulta existe
        Consulta consulta = consultaRepository.findById(recetaRequest.getConsulta().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Consulta no encontrada con el id " + recetaRequest.getConsulta().getId()));

        // Actualizar los datos de la receta
        existingReceta.setMedicamentos(recetaRequest.getMedicamentos());
        existingReceta.setIndicaciones(recetaRequest.getIndicaciones());
        existingReceta.setConsulta(consulta); // Actualizar la consulta asociada

        // Guardar los cambios en la base de datos
        Receta updatedReceta = recetaRepository.save(existingReceta);

        // Devolver la receta actualizada
        return ResponseEntity.ok(updatedReceta);
    }

    // Eliminar una receta
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MEDICO')")
    public ResponseEntity<Void> deleteReceta(@PathVariable Long id) {
        Receta receta = recetaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receta no encontrada con el id " + id));
        recetaRepository.delete(receta);
        return ResponseEntity.noContent().build();
    }

    //-------------------Recetas de un medico-------------------
    // Obtener las recetas por medicoId
    @GetMapping("/medico/{userId}")
    @PreAuthorize("hasRole('MEDICO') or hasRole('ADMIN')")
    public ResponseEntity<List<Object>> getRecetasByMedico(@PathVariable Long userId) {
        // Buscar el médico a partir del userId
        Medico medic = medicoRepository.findByUserId(userId);

        if (medic == null) {
            return ResponseEntity.notFound().build();  // Devuelve no encontrado si el médico no existe
        }

        // Obtener las consultas del médico para encontrar las recetas asociadas
        List<Consulta> consultas = consultaRepository.findByFichaMedicoId(medic.getId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").withZone(ZoneId.of("America/La_Paz"));
        if (consultas.isEmpty()) {
            return ResponseEntity.noContent().build();  // Si no hay consultas, devuelve no content
        }

        // Obtener las recetas asociadas a esas consultas
        List<Receta> recetas = recetaRepository.findByConsultaIn(consultas);

        if (recetas.isEmpty()) {
            return ResponseEntity.noContent().build();  // Si no hay recetas, devuelve no content
        }

        // Mapeo de recetas a un formato simplificado
        List<Object> recetaResponse = recetas.stream().map(receta -> {
            // Crear un mapa con los campos que necesitamos
            return Map.of(
                    "id", receta.getId(),
                    "consultaId", receta.getConsulta().getId(),  // Solo enviamos el ID de la consulta asociada
                    "medicamentos", receta.getMedicamentos(),
                    "indicaciones", receta.getIndicaciones(),
                    "fechaConsulta", formatter.format(receta.getFechaConsulta())
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(recetaResponse);
    }
}
