package com.bezkoder.spring.security.postgresql.controllers;

import com.bezkoder.spring.security.postgresql.models.*;
import com.bezkoder.spring.security.postgresql.repository.*;
import com.bezkoder.spring.security.postgresql.payload.request.CreatePacienteRequest;
import com.bezkoder.spring.security.postgresql.payload.response.MessageResponse;
import com.bezkoder.spring.security.postgresql.exceptions.ResourceNotFoundException;
import com.bezkoder.spring.security.postgresql.security.services.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:4200","*"})
@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    @Autowired
    private FichaNuevaRepository fichaNuevaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    PacienteRepository pacienteRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    // Obtener todos los pacientes
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Paciente>> getAllPacientes() {
        // Obtenemos todos los pacientes
        List<Paciente> pacientes = pacienteRepository.findAll();

        // Transformamos la lista de pacientes para incluir solo los parámetros necesarios
        List<Paciente> pacientesResponse = new ArrayList<>();

        for (Paciente paciente : pacientes) {
            // Creamos un nuevo objeto Paciente con los datos que necesitamos
            Paciente pacienteResponse = new Paciente();

            // Copiar solo los campos necesarios de Paciente
            pacienteResponse.setId(paciente.getId());
            pacienteResponse.setCi(paciente.getCi());
            pacienteResponse.setSexo(paciente.getSexo());
            pacienteResponse.setTelefono(paciente.getTelefono());
            pacienteResponse.setFechaNacimiento(paciente.getFechaNacimiento());
            pacienteResponse.setCodigoAsegurado(paciente.getCodigoAsegurado());

            // Crear el objeto User simplificado
            User userResponse = new User();
            userResponse.setId(paciente.getUser().getId());
            userResponse.setUsername(paciente.getUser().getUsername());
            userResponse.setEmail(paciente.getUser().getEmail());
            userResponse.setPassword("vacio");

            // Asignar el usuario simplificado al paciente
            pacienteResponse.setUser(userResponse);

            // Añadir a la lista final
            pacientesResponse.add(pacienteResponse);
        }

        // Devolver la lista de pacientes con la información simplificada
        return ResponseEntity.ok(pacientesResponse);
    }

    // Crear un nuevo Paciente
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> createPaciente(@RequestBody CreatePacienteRequest createPacienteRequest) {

        if (userRepository.existsByUsername(createPacienteRequest.getUser().getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: El nombre de usuario ya está en uso"));
        }

        if (userRepository.existsByEmail(createPacienteRequest.getUser().getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: El correo electrónico ya está en uso"));
        }
        if (pacienteRepository.existsBycodigoAsegurado(createPacienteRequest.getCodigoAsegurado())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: El codigo de Asegurado ya está en uso"));
        }

        User user = new User(createPacienteRequest.getUser().getUsername(), createPacienteRequest.getUser().getEmail(),
                encoder.encode(createPacienteRequest.getUser().getPassword()));

        // Asignar un rol de paciente
        Role pacienteRole = roleRepository.findByName(ERole.ROLE_PACIENTE)
                .orElseThrow(() -> new RuntimeException("Error: Role not found."));
        user.setRoles(new HashSet<>(Collections.singletonList(pacienteRole)));

        // Guardar el usuario
        User savedUser = userRepository.save(user);

        // Crear el Paciente
        Paciente newPaciente = new Paciente(
                createPacienteRequest.getCi(),
                createPacienteRequest.getSexo(),
                createPacienteRequest.getTelefono(),
                createPacienteRequest.getFechaNacimiento(),
                createPacienteRequest.getCodigoAsegurado(),
                savedUser
        );

        Paciente savedPaciente = pacienteRepository.save(newPaciente);

        return new ResponseEntity<>(savedPaciente, HttpStatus.CREATED);
    }

    // Obtener un paciente por su ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MEDICO') or hasRole('PACIENTE')")
    public ResponseEntity<Map<String, Object>> getPacienteById(@PathVariable Long id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente not found with id " + id));

        Map<String, Object> response = new HashMap<>();

        // Asignar los parámetros simplificados del paciente
        response.put("id", paciente.getId());
        response.put("ci", paciente.getCi());
        response.put("sexo", paciente.getSexo());
        response.put("telefono", paciente.getTelefono());
        response.put("fechaNacimiento", paciente.getFechaNacimiento());
        response.put("codigoAsegurado", paciente.getCodigoAsegurado());

        // Obtener el usuario asociado al paciente y simplificar la información
        User user = paciente.getUser();
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("id", user.getId());
        userResponse.put("username", user.getUsername());
        userResponse.put("email", user.getEmail());
        userResponse.put("password", "");  // No devolvemos la contraseña

        // Asignar el usuario simplificado al paciente
        response.put("user", userResponse);

        // Devolver la respuesta simplificada
        return ResponseEntity.ok(response);
    }



    // Eliminar un paciente
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePaciente(@PathVariable Long id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente not found with id " + id));

        // Eliminar el User asociado al Paciente
        User user = paciente.getUser();
        pacienteRepository.delete(paciente);
        userRepository.delete(user);

        return ResponseEntity.noContent().build();
    }

    //lista devuelve los usuarios con id de userId
    @GetMapping("/byUser/{userId}")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<List<Paciente>> getPacientesByUserId(@PathVariable Long userId) {
        System.out.println("llegoa byUser userId");
        // Buscar los pacientes asociados al userId proporcionado
        List<Paciente> pacientes = pacienteRepository.getPacientesByUserId (userId);

        // Si no se encuentran pacientes, devolver un 404 Not Found
        if (pacientes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Paciente> pacientesResponse = new ArrayList<>();

        for (Paciente paciente : pacientes) {
            // Creamos un nuevo objeto Paciente con los datos que necesitamos
            Paciente pacienteResponse = new Paciente();

            // Copiar solo los campos necesarios de Paciente
            pacienteResponse.setId(paciente.getId());
            pacienteResponse.setCi(paciente.getCi());
            pacienteResponse.setSexo(paciente.getSexo());
            pacienteResponse.setTelefono(paciente.getTelefono());
            pacienteResponse.setFechaNacimiento(paciente.getFechaNacimiento());
            pacienteResponse.setCodigoAsegurado(paciente.getCodigoAsegurado());

            // Crear el objeto User simplificado
            User userResponse = new User();
            userResponse.setId(paciente.getUser().getId());
            userResponse.setUsername(paciente.getUser().getUsername());
            userResponse.setEmail(paciente.getUser().getEmail());
            userResponse.setPassword("vacio");

            // Asignar el usuario simplificado al paciente
            pacienteResponse.setUser(userResponse);

            // Añadir a la lista final
            pacientesResponse.add(pacienteResponse);
        }

        // Devolver la lista de pacientes con la información simplificada
        return ResponseEntity.ok(pacientesResponse);
    }

    //-------------------pacintes de un medico-------------------

    @GetMapping("/medico/{userId}")
    @PreAuthorize("hasRole('MEDICO') or hasRole('ADMIN')")
    public ResponseEntity<List<Paciente>> getPacientesByMedico(@PathVariable Long userId) {
        Medico medico = medicoRepository.findByUserId(userId);
        if (medico == null) {
            return ResponseEntity.notFound().build();
        }

        List<FichaNueva> fichas = fichaNuevaRepository.findByMedicoId(medico.getId());
        if (fichas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<Paciente> pacientes = pacienteService.getPacientesPorFichas(fichas);
        if (pacientes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        // Eliminar la contraseña antes de enviar la respuesta
        pacientes.forEach(paciente -> {
            if (paciente.getUser() != null) {
                paciente.getUser().setPassword("vacio");
            }
        });

        return ResponseEntity.ok(pacientes);
    }



}

