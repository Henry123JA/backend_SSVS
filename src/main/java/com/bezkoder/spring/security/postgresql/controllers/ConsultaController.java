package com.bezkoder.spring.security.postgresql.controllers;

import com.bezkoder.spring.security.postgresql.models.Consulta;
import com.bezkoder.spring.security.postgresql.models.FichaNueva;
import com.bezkoder.spring.security.postgresql.exceptions.ResourceNotFoundException;
import com.bezkoder.spring.security.postgresql.models.Medico;
import com.bezkoder.spring.security.postgresql.repository.ConsultaRepository;
import com.bezkoder.spring.security.postgresql.repository.MedicoRepository;
import com.bezkoder.spring.security.postgresql.repository.FichaNuevaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.bezkoder.spring.security.postgresql.payload.request.CreateConsultaRequest;
import java.time.format.DateTimeFormatter;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:4200", "*"})
@RestController
@RequestMapping("/api/consultas")
public class ConsultaController {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private FichaNuevaRepository fichaNuevaRepository;

    // Obtener todas las consultas
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Object>> getAllConsultas() {
        List<Consulta> consultas = consultaRepository.findAll();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").withZone(ZoneId.of("America/La_Paz"));
        // Mapeo de consultas a un formato simplificado
        List<Object> consultaResponse = consultas.stream().map(consulta -> {
            String formattedFechaConsulta = formatter.format(consulta.getFechaConsulta());
            // Crear un mapa con los campos que necesitamos
            return Map.of(
                    "id", consulta.getId(),
                    "fichaId", consulta.getFicha().getId(),  // Solo enviamos el ID de la ficha
                    "sintomas", consulta.getSintomas(),
                    "diagnostico", consulta.getDiagnostico(),
                    "tratamiento", consulta.getTratamiento(),
                    "notas", consulta.getNotas(),
                    "fechaConsulta", formattedFechaConsulta
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(consultaResponse);
    }

    // Obtener una consulta por su ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MEDICO') or hasRole('PACIENTE') or hasRole('ADMIN')")
    public ResponseEntity<Consulta> getConsultaById(@PathVariable Long id) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta no encontrada con el id " + id));
        return ResponseEntity.ok(consulta);
    }

    // Obtener las consultas asociadas a una ficha
    @GetMapping("/ficha/{fichaId}")
    @PreAuthorize("hasRole('MEDICO') or hasRole('PACIENTE')")
    public ResponseEntity<List<Consulta>> getConsultasByFicha(@PathVariable Long fichaId) {
        FichaNueva ficha = fichaNuevaRepository.findById(fichaId)
                .orElseThrow(() -> new ResourceNotFoundException("Ficha no encontrada con el id " + fichaId));

        List<Consulta> consultas = consultaRepository.findByFichaId(fichaId);
        return ResponseEntity.ok(consultas);
    }

    // Crear una nueva consulta
    @PostMapping
    @PreAuthorize("hasRole('MEDICO') or hasRole('ADMIN')")
    public ResponseEntity<Consulta> createConsulta(@RequestBody CreateConsultaRequest request) {
        // Buscar la ficha correspondiente al fichaId
        FichaNueva ficha = fichaNuevaRepository.findById(request.getFichaId())
                .orElseThrow(() -> new RuntimeException("Ficha no encontrada"));

        ficha.setEstado("atendido");
        FichaNueva newFicha = fichaNuevaRepository.save(ficha);
        // Crear una nueva instancia de Consulta
        Consulta consulta = new Consulta();
        consulta.setFicha(newFicha);
        consulta.setSintomas(request.getSintomas());
        consulta.setDiagnostico(request.getDiagnostico());
        consulta.setTratamiento(request.getTratamiento());
        consulta.setNotas(request.getNotas());
        //consulta.setFechaConsulta(request.getFechaConsulta().);

        // Guardar la consulta en la base de datos
        Consulta savedConsulta = consultaRepository.save(consulta);

        // Retornar la consulta guardada
        return ResponseEntity.ok(savedConsulta);

    }

    // Actualizar una consulta existente
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MEDICO') or hasRole('ADMIN')")
    public ResponseEntity<Consulta> updateConsulta(@PathVariable Long id, @RequestBody Consulta consultaRequest) {
        // Verificar si la consulta existe
        Consulta existingConsulta = consultaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta no encontrada"));

        // Verificar si la ficha existe
        FichaNueva ficha = fichaNuevaRepository.findById(consultaRequest.getFicha().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Ficha no encontrada"));

        // Actualizar los datos de la consulta
        existingConsulta.setSintomas(consultaRequest.getSintomas());
        existingConsulta.setDiagnostico(consultaRequest.getDiagnostico());
        existingConsulta.setTratamiento(consultaRequest.getTratamiento());
        existingConsulta.setNotas(consultaRequest.getNotas());
        existingConsulta.setFicha(ficha);

        // Guardar los cambios en la base de datos
        Consulta updatedConsulta = consultaRepository.save(existingConsulta);

        // Devolver la consulta actualizada
        return ResponseEntity.ok(updatedConsulta);
    }

    // Eliminar una consulta
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MEDICO')")
    public ResponseEntity<Void> deleteConsulta(@PathVariable Long id) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta no encontrada con el id " + id));
        consultaRepository.delete(consulta);
        return ResponseEntity.noContent().build();
    }

    //-------------------consultas de un medico-------------------
    // Obtener las consultas por medicoId
    @GetMapping("/medico/{userId}")
    @PreAuthorize("hasRole('MEDICO') or hasRole('ADMIN')")
    public ResponseEntity<List<Object>> getConsultasByMedico(@PathVariable Long userId) {
        // Buscar las consultas cuyo medicoId en la ficha coincida con el parámetro
        Medico medic = medicoRepository.findByUserId(userId);

        List<Consulta> consultas = consultaRepository.findByFichaMedicoId(medic.getId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").withZone(ZoneId.of("America/La_Paz"));
        if (consultas.isEmpty()) {
            return ResponseEntity.noContent().build();  // Devuelve no content si no hay consultas
        }
        // Mapeo de consultas a un formato simplificado
        List<Object> consultaResponse = consultas.stream().map(consulta -> {
            String formattedFechaConsulta = formatter.format(consulta.getFechaConsulta());
            // Crear un mapa con los campos que necesitamos
            return Map.of(
                    "id", consulta.getId(),
                    "fichaId", consulta.getFicha().getId(),  // Solo enviamos el ID de la ficha
                    "sintomas", consulta.getSintomas(),
                    "diagnostico", consulta.getDiagnostico(),
                    "tratamiento", consulta.getTratamiento(),
                    "notas", consulta.getNotas(),
                    "fechaConsulta", formattedFechaConsulta
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(consultaResponse);
    }

    // Obtener consultas por pacienteId
    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasRole('MEDICO') or hasRole('PACIENTE') or hasRole('ADMIN')")
    public ResponseEntity<List<Object>> getConsultasByPaciente(@PathVariable Long pacienteId) {
        // Buscar las fichas asociadas al paciente
        List<FichaNueva> fichas = fichaNuevaRepository.findByPacienteId(pacienteId);

        if (fichas.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron fichas para el paciente con id " + pacienteId);
        }

        // Extraer las consultas asociadas a las fichas encontradas
        List<Consulta> consultas = fichas.stream()
                .flatMap(ficha -> consultaRepository.findByFichaId(ficha.getId()).stream())
                .collect(Collectors.toList());

        if (consultas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").withZone(ZoneId.of("America/La_Paz"));

        List<Object> consultaResponse = consultas.stream().map(consulta -> {
            String formattedFechaConsulta = formatter.format(consulta.getFechaConsulta());
            return Map.of(
                    "id", consulta.getId(),
                    "fichaId", consulta.getFicha().getId(),
                    "sintomas", consulta.getSintomas(),
                    "diagnostico", consulta.getDiagnostico(),
                    "tratamiento", consulta.getTratamiento(),
                    "notas", consulta.getNotas(),
                    "fechaConsulta", formattedFechaConsulta
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(consultaResponse);
    }


}
