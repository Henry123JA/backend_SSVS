
package com.bezkoder.spring.security.postgresql.controllers;

import com.bezkoder.spring.security.postgresql.models.BloqueHorario;
import com.bezkoder.spring.security.postgresql.models.Horario;
import com.bezkoder.spring.security.postgresql.models.Medico;
import com.bezkoder.spring.security.postgresql.payload.dto.BloqueHorarioDTO;
import com.bezkoder.spring.security.postgresql.repository.HorarioRepository;
import com.bezkoder.spring.security.postgresql.repository.MedicoRepository;
import com.bezkoder.spring.security.postgresql.exceptions.ResourceNotFoundException;
import com.bezkoder.spring.security.postgresql.security.services.HorarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:4200", "*"})
@RestController
@RequestMapping("/api/horarios")
public class HorarioController {

    @Autowired
    private HorarioRepository horarioRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private HorarioService horarioService;

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

    //----------------------------------------------para crear los bloques-----------------------------------------
    // Crear bloques de horarios asociados al horario recibido
    @PostMapping("/crear-bloques")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Horario> createHorariosConBloques(@RequestBody Horario horarioRequest) {
        // Buscar al médico asociado al horario
        Medico medico = medicoRepository.findById(horarioRequest.getMedico().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Medico no encontrado"));
        horarioRequest.setMedico(medico);
        horarioRequest.setEstado("activo");
        Horario newHorario = horarioRepository.save(horarioRequest);
        try {
            horarioService.crearHorariosConBloques(newHorario);
            return new ResponseEntity<>(newHorario, HttpStatus.CREATED);
        } catch (Exception e) {
            Horario errorHorario = new Horario();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorHorario);
        }
    }


    @GetMapping("/crear-bloques/{horarioId}")
    @PreAuthorize("hasRole('PACIENTE') or hasRole('ADMIN')")
    public ResponseEntity<List<BloqueHorarioDTO>> getAllBloquesByHorarioId(@PathVariable Long horarioId) {
        // Buscar el horario
        Horario horario = horarioRepository.findById(horarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Horario no encontrado con el id " + horarioId));

        // Obtener los bloques asociados a este horario
        List<BloqueHorario> bloques = horarioService.getBloquesByHorario(horario);

        // Convertir los bloques en DTOs
        List<BloqueHorarioDTO> bloquesDTO = bloques.stream()
                .map(bloque -> new BloqueHorarioDTO(bloque))
                .collect(Collectors.toList());

        return ResponseEntity.ok(bloquesDTO);
    }


    // Eliminar un bloque de horario específico
    @DeleteMapping("/crear-bloques/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBloqueHorario(@PathVariable Long id) {
        // Buscar el bloque de horario
        BloqueHorario bloque = horarioService.getBloqueById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bloque de horario no encontrado con el id " + id));


        // Eliminar el bloque
        horarioService.deleteBloque(bloque);
        return ResponseEntity.noContent().build();
    }
//-------------------------horario-medico--------------------------------------
@GetMapping("/horarios-medico/{medicoId}")
@PreAuthorize("hasRole('PACIENTE') or hasRole('ADMIN')")
public ResponseEntity<List<Horario>> getHorariosByMedicoId(@PathVariable Long medicoId) {
    // Buscar al médico por su ID
    Medico medico = medicoRepository.findById(medicoId)
            .orElseThrow(() -> new ResourceNotFoundException("Medico no encontrado con id " + medicoId));

    // Obtener todos los horarios asociados al médico
    List<Horario> horarios = horarioRepository.findByMedico(medico);

    // Si no se encuentran horarios, retornar una respuesta vacía
    if (horarios.isEmpty()) {
        return ResponseEntity.noContent().build();
    }

    // Devolver la lista de horarios
    return ResponseEntity.ok(horarios);
}


}

