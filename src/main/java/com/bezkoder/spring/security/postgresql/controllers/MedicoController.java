package com.bezkoder.spring.security.postgresql.controllers;

import com.bezkoder.spring.security.postgresql.payload.dto.MedicoDTO;
import com.bezkoder.spring.security.postgresql.repository.*;
import com.bezkoder.spring.security.postgresql.security.services.MedicoService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Collectors;

import com.bezkoder.spring.security.postgresql.exceptions.ResourceNotFoundException;
import com.bezkoder.spring.security.postgresql.payload.response.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.bezkoder.spring.security.postgresql.models.*;
import com.bezkoder.spring.security.postgresql.payload.request.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/medicos")
public class MedicoController {

    @Autowired
    MedicoRepository medicoRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private EspecialidadRepository especialidadRepository;

    @Autowired
    private MedicoEspecialidadRepository medicoEspecialidadRepository;

    @Autowired
    private MedicoService medicoService;

    @Autowired
    PasswordEncoder encoder;

    // Obtener todos los médicos
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Medico>> getAllMedicos() {
        // Obtenemos todos los médicos
        List<Medico> medicos = medicoRepository.findAll();

        // Transformamos la lista de médicos para incluir solo los parámetros necesarios
        List<Medico> medicosResponse = new ArrayList<>();

        for (Medico medico : medicos) {
            // Creamos un nuevo objeto Medico con los datos que necesitamos
            Medico medicoResponse = new Medico();

            // Copiar solo los campos necesarios de Medico
            medicoResponse.setId(medico.getId());
            medicoResponse.setCi(medico.getCi());
            medicoResponse.setSexo(medico.getSexo());
            medicoResponse.setTelefono(medico.getTelefono());

            // Crear el objeto User simplificado
            User userResponse = new User();
            userResponse.setId(medico.getUser().getId());
            userResponse.setUsername(medico.getUser().getUsername());
            userResponse.setEmail(medico.getUser().getEmail());
            userResponse.setPassword("vacio");

            // Asignar el usuario simplificado al médico
            medicoResponse.setUser(userResponse);

            // Añadir a la lista final
            medicosResponse.add(medicoResponse);
        }

        // Devolver la lista de médicos con la información simplificada
        return ResponseEntity.ok(medicosResponse);
    }


    // Crear un nuevo Medico
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> createMedico(@RequestBody CreateMedicoRequest createMedicoRequest) {
       /* System.out.println("llego al controlador de medico...");
        System.out.println("de username: "+ createMedicoRequest.getUser().getUsername());
        System.out.println("de password: "+ createMedicoRequest.getUser().getPassword());
        System.out.println("de email: "+ createMedicoRequest.getUser().getEmail());
        System.out.println("de ci: "+ createMedicoRequest.getCi());
        System.out.println("de sexo: "+ createMedicoRequest.getSexo());
        System.out.println("de telefono: "+ createMedicoRequest.getTelefono());*/

        if (userRepository.existsByUsername(createMedicoRequest.getUser().getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: El nombre de usuario ya está en uso"));
        }

        if (userRepository.existsByEmail(createMedicoRequest.getUser().getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: El correo electrónico ya está en uso"));
        }

        User user = new User(createMedicoRequest.getUser().getUsername(), createMedicoRequest.getUser().getEmail(),
                encoder.encode(createMedicoRequest.getUser().getPassword()));

        Role medicoRole = roleRepository.findByName(ERole.ROLE_MEDICO)
                .orElseThrow(() -> new RuntimeException("Error: Role not found."));
        user.setRoles(new HashSet<>(Collections.singletonList(medicoRole)));

        // Guardar el usuario
        User savedUser = userRepository.save(user);

        // Crear el Medico
        Medico newMedico = new Medico(
                createMedicoRequest.getCi(),
                createMedicoRequest.getSexo(),
                createMedicoRequest.getTelefono(),
                savedUser
        );

        Medico savedMedico = medicoRepository.save(newMedico);

        return new ResponseEntity<>(savedMedico, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getMedicoById(@PathVariable Long id) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medico not found with id " + id));

        Map<String, Object> response = new HashMap<>();

        // Asignar los parámetros simplificados del médico
        response.put("id", medico.getId());
        response.put("ci", medico.getCi());
        response.put("sexo", medico.getSexo());
        response.put("telefono", medico.getTelefono());

        // Obtener el usuario asociado al médico y simplificar la información
        User user = medico.getUser();
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("id", user.getId());
        userResponse.put("username", user.getUsername());
        userResponse.put("email", user.getEmail());
        userResponse.put("password", "");  // No devolvemos la contraseña

        // Asignar el usuario simplificado al médico
        response.put("user", userResponse);

        // Devolver la respuesta simplificada
        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Medico> updateMedico(@PathVariable Long id, @RequestBody UpdateMedicoRequest updateMedicoRequest) {

        System.out.println("entro al controlador......");
        MedicoDTO medicoDTO = medicoService.getMedico(id);
        Medico updatedMedico = medicoService.updateMedico(id, updateMedicoRequest);
        System.out.println("salio del controlador......");

        return ResponseEntity.ok(updatedMedico);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMedico(@PathVariable Long id) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medico not found with id " + id));

        // Eliminar el User asociado al Medico
        User user = medico.getUser();
        medicoRepository.delete(medico);
        userRepository.delete(user);
/*
        // Eliminar el Medico
        medicoRepository.delete(medico);*/
        return ResponseEntity.noContent().build();
    }
    //------------------------------de  especialidad-medico------------------------------------
    // Asignar especialidad a un médico
    @PostMapping("/{medicoId}/especialidades/{especialidadId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> asignarEspecialidad(@PathVariable Long medicoId, @PathVariable Long especialidadId) {
        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new ResourceNotFoundException("Medico no encontrado con id " + medicoId));

        Especialidad especialidad = especialidadRepository.findById(especialidadId)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada con id " + especialidadId));

        medico.addEspecialidad(especialidad);
        medicoRepository.save(medico);

        return ResponseEntity.ok(new MessageResponse("Especialidad asignada con éxito"));
    }

    // Eliminar especialidad de un médico
    @DeleteMapping("/{medicoId}/especialidades/{especialidadId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> eliminarEspecialidad(@PathVariable Long medicoId, @PathVariable Long especialidadId) {
        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new ResourceNotFoundException("Medico no encontrado con id " + medicoId));

        Especialidad especialidad = especialidadRepository.findById(especialidadId)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada con id " + especialidadId));

        medico.removeEspecialidad(especialidad);
        medicoRepository.save(medico);

        return ResponseEntity.ok(new MessageResponse("Especialidad eliminada del médico con éxito"));
    }
    // Obtener la lista de todos los médicos con sus especialidades asignadas y el usuario relacionado
    @GetMapping("/medicos-con-especialidades")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getAllMedicoEspecialidades() {
        // Obtener todas las relaciones de médicos y especialidades
        List<MedicoEspecialidad> medicoEspecialidades = medicoEspecialidadRepository.findAll();

        // Transformar las relaciones en un formato más adecuado para la respuesta
        List<Map<String, Object>> responseList = medicoEspecialidades.stream()
                .map(medicoEspecialidad -> {
                    Map<String, Object> responseMap = new HashMap<>();

                    // Obtener el médico y especialidad
                    Medico medico = medicoEspecialidad.getMedico();
                    Especialidad especialidad = medicoEspecialidad.getEspecialidad();

                    // Crear el objeto de respuesta para el médico
                    Map<String, Object> medicoResponse = new HashMap<>();
                    medicoResponse.put("medicoId", medico.getId());
                    medicoResponse.put("medicoCi", medico.getCi());
                    //medicoResponse.put("medicoSexo", medico.getSexo());
                    //medicoResponse.put("medicoTelefono", medico.getTelefono());

                    // Obtener el usuario asociado al médico
                    User user = medico.getUser();
                    Map<String, Object> userResponse = new HashMap<>();
                    userResponse.put("userId", user.getId());
                    userResponse.put("username", user.getUsername());
                    userResponse.put("email", user.getEmail());

                    // Crear el objeto de respuesta para la especialidad
                    Map<String, Object> especialidadResponse = new HashMap<>();
                    especialidadResponse.put("especialidadId", especialidad.getId());
                    especialidadResponse.put("especialidadNombre", especialidad.getNombre());
                    //especialidadResponse.put("especialidadDescripcion", especialidad.getDescripcion());

                    // Agregar los datos a la respuesta final
                    responseMap.put("medico", medicoResponse);
                    responseMap.put("user", userResponse);
                    responseMap.put("especialidad", especialidadResponse);

                    return responseMap;
                })
                .collect(Collectors.toList());

        // Devolver la lista de relaciones médico-especialidad con información del usuario
        return ResponseEntity.ok(responseList);
    }


}

