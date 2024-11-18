package com.bezkoder.spring.security.postgresql.controllers;

import com.bezkoder.spring.security.postgresql.payload.dto.PacienteDTO;
import com.bezkoder.spring.security.postgresql.payload.request.CreatePacienteRequest;
import com.bezkoder.spring.security.postgresql.payload.response.MessageResponse;
import com.bezkoder.spring.security.postgresql.repository.PacienteRepository;
import com.bezkoder.spring.security.postgresql.repository.UserRepository;
import com.bezkoder.spring.security.postgresql.repository.RoleRepository;
import com.bezkoder.spring.security.postgresql.models.Paciente;
import com.bezkoder.spring.security.postgresql.models.User;
import com.bezkoder.spring.security.postgresql.models.Role;
import com.bezkoder.spring.security.postgresql.models.ERole;
import com.bezkoder.spring.security.postgresql.security.services.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Collections;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    @Autowired
    PacienteRepository pacienteRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    PasswordEncoder encoder;

    // Crear un nuevo Paciente
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> createPaciente(@RequestBody CreatePacienteRequest createPacienteRequest) {
        // Verificar si el usuario ya existe
        if (userRepository.existsByUsername(createPacienteRequest.getUser().getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: El nombre de usuario ya está en uso"));
        }

        if (userRepository.existsByEmail(createPacienteRequest.getUser().getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: El correo electrónico ya está en uso"));
        }

        // Crear el usuario
        User user = new User(createPacienteRequest.getUser().getUsername(),
                createPacienteRequest.getUser().getEmail(),
                encoder.encode(createPacienteRequest.getUser().getPassword()));

        // Asignar el rol de paciente al usuario
        Role pacienteRole = roleRepository.findByName(ERole.ROLE_PACIENTE)
                .orElseThrow(() -> new RuntimeException("Error: Role not found."));
        user.setRoles(new HashSet<>(Collections.singletonList(pacienteRole)));

        // Guardar el usuario en la base de datos
        User savedUser = userRepository.save(user);

        // Crear el paciente asociando el usuario
        Paciente newPaciente = new Paciente(
                createPacienteRequest.getCi(),
                createPacienteRequest.getSexo(),
                createPacienteRequest.getTelefono(),
                savedUser // Asociamos el usuario al paciente
        );

        // Guardar el paciente en la base de datos
        Paciente savedPaciente = pacienteRepository.save(newPaciente);

        return new ResponseEntity<>(savedPaciente, HttpStatus.CREATED);
    }

    // Obtener todos los pacientes
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllPacientes() {
        return ResponseEntity.ok(pacienteRepository.findAll());
    }

    // Obtener un paciente por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getPacienteById(@PathVariable Long id) {
        Optional<Paciente> paciente = pacienteRepository.findById(id);
        if (paciente.isPresent()) {
            return ResponseEntity.ok(paciente.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Error: Paciente no encontrado"));
        }
    }

    // Actualizar un paciente
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatePaciente(@PathVariable Long id, @RequestBody CreatePacienteRequest createPacienteRequest) {
        Optional<Paciente> pacienteData = pacienteRepository.findById(id);

        if (pacienteData.isPresent()) {
            Paciente paciente = pacienteData.get();

            paciente.setCi(createPacienteRequest.getCi());
            paciente.setSexo(createPacienteRequest.getSexo());
            paciente.setTelefono(createPacienteRequest.getTelefono());

            Paciente updatedPaciente = pacienteRepository.save(paciente);
            return ResponseEntity.ok(updatedPaciente);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Error: Paciente no encontrado"));
        }
    }

    // Eliminar un paciente
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePaciente(@PathVariable Long id) {
        if (pacienteRepository.existsById(id)) {
            pacienteRepository.deleteById(id);
            return ResponseEntity.ok(new MessageResponse("Paciente eliminado exitosamente"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Error: Paciente no encontrado"));
        }
    }
}