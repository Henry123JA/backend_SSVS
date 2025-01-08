
package com.bezkoder.spring.security.postgresql.controllers;

import com.bezkoder.spring.security.postgresql.models.Examen;
import com.bezkoder.spring.security.postgresql.models.Consulta;
import com.bezkoder.spring.security.postgresql.models.Medico;
import com.bezkoder.spring.security.postgresql.models.Receta;
import com.bezkoder.spring.security.postgresql.payload.request.CreateExamenRequest;
import com.bezkoder.spring.security.postgresql.payload.request.CreateRecetaRequest;
import com.bezkoder.spring.security.postgresql.repository.ExamenRepository;
import com.bezkoder.spring.security.postgresql.repository.ConsultaRepository;
import com.bezkoder.spring.security.postgresql.exceptions.ResourceNotFoundException;
import com.bezkoder.spring.security.postgresql.repository.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
        import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:4200", "*"})
@RestController
@RequestMapping("/api/examenes")
public class ExamenController {

    @Autowired
    private ExamenRepository examenRepository;

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    // Obtener todos los exámenes
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Object>> getAllExamenes() {
        List<Examen> examenes = examenRepository.findAll();

        // Mapeo de exámenes a un formato simplificado
        List<Object> examenResponse = examenes.stream().map(examen -> {
            return Map.of(
                    "id", examen.getId(),
                    "consultaId", examen.getConsulta().getId(),
                    "laboratorioNombre", examen.getLaboratorioNombre(),
                    "tipoExamen", examen.getTipoExamen(),
                    "resultados", examen.getResultados(),
                    "fechaExamen", examen.getFechaExamen()
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(examenResponse);
    }

    // Obtener un examen por su ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MEDICO') or hasRole('PACIENTE') or hasRole('ADMIN')")
    public ResponseEntity<Object> getExamenById(@PathVariable Long id) {
        Examen examen = examenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Examen no encontrado con el id " + id));
        // Mapeo simplificado del examen
        Map<String, Object> examenResponse = Map.of(
                "id", examen.getId(),
                "consultaId", examen.getConsulta().getId(),
                "laboratorioNombre", examen.getLaboratorioNombre(),
                "tipoExamen", examen.getTipoExamen(),
                "resultados", examen.getResultados(),
                "fechaExamen", examen.getFechaExamen()
        );

        return ResponseEntity.ok(examenResponse);
    }

    // Obtener el examen asociado a una consulta
    @GetMapping("/consulta/{consultaId}")
    @PreAuthorize("hasRole('MEDICO') or hasRole('PACIENTE') or hasRole('ADMIN')")
    public ResponseEntity<Object> getExamenByConsulta(@PathVariable Long consultaId) {
        Examen examen = examenRepository.findByConsultaId(consultaId)
                .orElseThrow(() -> new ResourceNotFoundException("Examen no encontrado para la consulta con id " + consultaId));
        // Mapeo simplificado del examen
        Map<String, Object> examenResponse = Map.of(
                "id", examen.getId(),
                "consultaId", examen.getConsulta().getId(),
                "laboratorioNombre", examen.getLaboratorioNombre(),
                "tipoExamen", examen.getTipoExamen(),
                "resultados", examen.getResultados(),
                "fechaExamen", examen.getFechaExamen()
        );

        return ResponseEntity.ok(examenResponse);
    }

    @GetMapping("/consulta2/{consultaId}")
    @PreAuthorize("hasRole('MEDICO') or hasRole('PACIENTE') or hasRole('ADMIN')")
    public ResponseEntity<Object> getExamenesByConsulta(@PathVariable Long consultaId) {
        List<Examen> examenes = examenRepository.findAllByConsultaId(consultaId);

        if (examenes.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "message", "No hay exámenes asociados a la consulta con id " + consultaId
            ));
        }

        List<Map<String, Object>> examenesResponse = examenes.stream().map(examen -> {
            Map<String, Object> examenMap = new HashMap<>();
            examenMap.put("id", examen.getId());
            examenMap.put("consultaId", examen.getConsulta().getId());
            examenMap.put("laboratorioNombre", examen.getLaboratorioNombre());
            examenMap.put("tipoExamen", examen.getTipoExamen());
            examenMap.put("resultados", examen.getResultados());
            examenMap.put("fechaExamen", examen.getFechaExamen());
            return examenMap;
        }).toList();

        System.out.println("cantidad de examenes:"+examenesResponse.size());
        return ResponseEntity.ok(examenesResponse);
    }



    // Crear un nuevo examen
    @PostMapping
    @PreAuthorize("hasRole('MEDICO') or hasRole('ADMIN')")
    public ResponseEntity<Examen> createExamen(@RequestBody CreateExamenRequest examenRequest  ) {
        // Buscar la consulta correspondiente al consultaId
        Consulta consulta = consultaRepository.findById(examenRequest.getConsultaId())
                .orElseThrow(() -> new ResourceNotFoundException("Consulta no encontrada con el id " + examenRequest.getConsultaId()));

        // Crear una nueva instancia de Examen
        Examen examen = new Examen();
        examen.setLaboratorioNombre(examenRequest.getLaboratorioNombre());
        examen.setTipoExamen(examenRequest.getTipoExamen());
        examen.setResultados(examenRequest.getResultados());
        examen.setConsulta(consulta);
        examen.setFechaExamen(examenRequest.getFechaExamen());
        // Guardar el examen en la base de datos
        Examen savedExamen = examenRepository.save(examen);

        // Retornar el examen guardado
        return ResponseEntity.ok(savedExamen);
    }

    // Actualizar un examen existente
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MEDICO') or hasRole('ADMIN')")
    public ResponseEntity<Examen> updateExamen(@PathVariable Long id, @RequestBody Examen examenRequest) {
        // Verificar si el examen existe
        Examen existingExamen = examenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Examen no encontrado con el id " + id));

        // Verificar si la consulta existe
        Consulta consulta = consultaRepository.findById(examenRequest.getConsulta().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Consulta no encontrada con el id " + examenRequest.getConsulta().getId()));

        // Actualizar los datos del examen
        existingExamen.setLaboratorioNombre(examenRequest.getLaboratorioNombre());
        existingExamen.setTipoExamen(examenRequest.getTipoExamen());
        existingExamen.setResultados(examenRequest.getResultados());
        existingExamen.setConsulta(consulta); // Actualizar la consulta asociada

        // Guardar los cambios en la base de datos
        Examen updatedExamen = examenRepository.save(existingExamen);

        // Devolver el examen actualizado
        return ResponseEntity.ok(updatedExamen);
    }

    // Eliminar un examen
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MEDICO')")
    public ResponseEntity<Void> deleteExamen(@PathVariable Long id) {
        Examen examen = examenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Examen no encontrado con el id " + id));
        examenRepository.delete(examen);
        return ResponseEntity.noContent().build();
    }

    //-------------------Exámenes de una consulta-------------------
    // Obtener exámenes por médicoId
    @GetMapping("/medico/{userId}")
    @PreAuthorize("hasRole('MEDICO') or hasRole('ADMIN')")
    public ResponseEntity<List<Object>> getExamenesByMedico(@PathVariable Long userId) {
        // Buscar el médico a partir del userId
        Medico medic = medicoRepository.findByUserId(userId);

        if (medic == null) {
            return ResponseEntity.notFound().build();  // Devuelve no encontrado si el médico no existe
        }

        // Obtener las consultas del médico para encontrar las recetas asociadas
        List<Consulta> consultas = consultaRepository.findByFichaMedicoId(medic.getId());
        if (consultas.isEmpty()) {
            return ResponseEntity.noContent().build();  // Si no hay consultas, devuelve no content
        }

        // Obtener las examenes asociadas a esas consultas
        List<Examen> examenes = examenRepository.findByConsultaIn(consultas);

        if (examenes.isEmpty()) {
            return ResponseEntity.noContent().build();  // Si no hay recetas, devuelve no content
        }

        // Mapeo de recetas a un formato simplificado
        List<Object> examenResponse = examenes.stream().map(examen -> {
            // Crear un mapa con los campos que necesitamos
            return Map.of(
                    "id", examen.getId(),
                    "consultaId", examen.getConsulta().getId(),
                    "laboratorioNombre", examen.getLaboratorioNombre(),
                    "tipoExamen", examen.getTipoExamen(),
                    "resultados", examen.getResultados(),
                    "fechaExamen", examen.getFechaExamen()
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(examenResponse);
    }

}
