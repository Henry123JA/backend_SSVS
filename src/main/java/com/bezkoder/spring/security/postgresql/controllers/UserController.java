
package com.bezkoder.spring.security.postgresql.controllers;

import java.util.*;
import java.util.stream.Collectors;

import com.bezkoder.spring.security.postgresql.exceptions.ResourceNotFoundException;
//import com.bezkoder.spring.security.postgresql.models.User;
import com.bezkoder.spring.security.postgresql.payload.response.MessageResponse;
import com.bezkoder.spring.security.postgresql.repository.RoleRepository;
import com.bezkoder.spring.security.postgresql.repository.UserRepository;
import com.bezkoder.spring.security.postgresql.repository.MedicoRepository;
import com.bezkoder.spring.security.postgresql.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.bezkoder.spring.security.postgresql.models.*;/*
import com.bezkoder.spring.security.postgresql.models.ERole;
import com.bezkoder.spring.security.postgresql.models.Role;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;*/
import com.bezkoder.spring.security.postgresql.payload.request.*;

//import static jdk.internal.org.jline.utils.Colors.s;

@CrossOrigin(origins = {"http://localhost:4200","*"})
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    MedicoRepository medicoRepository;

    @Autowired
    PacienteRepository pacienteRepository;

    @Autowired
    PasswordEncoder encoder;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> createUser(@RequestBody CreateUserRequest createUserRequest) {
        boolean isMedico = false;
        boolean isPaciente = false;
        // Verificar si el usuario ya existe por nombre de usuario o correo
        if (userRepository.existsByUsername(createUserRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: El nombre de usuario ya está en uso"));
        }

        if (userRepository.existsByEmail(createUserRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: El nombre de usuario ya está en uso"));
        }

        User user = new User(createUserRequest.getUsername(), createUserRequest.getEmail(),
                encoder.encode(createUserRequest.getPassword()));

        // Si no se proporcionan roles, asignar el rol por defecto ROLE_PACIENTE
        if (createUserRequest.getRoles() == null || createUserRequest.getRoles().isEmpty()) {
            isPaciente = !isPaciente;
            Role defaultRole = roleRepository.findByName(ERole.ROLE_PACIENTE)
                    .orElseThrow(() -> new RuntimeException("Error: Role not found."));
            user.setRoles(new HashSet<>(Collections.singletonList(defaultRole)));
        } else {
            // Convertir los roles en strings a objetos Role
            Set<Role> roles = new HashSet<>();
            for (String roleName : createUserRequest.getRoles()) {
                switch (roleName) {
                    case "ROLE_ADMIN":{
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role not found."));
                        roles.add(adminRole);
                        break;}
                    case "ROLE_MEDICO":{
                        Role medicoRole = roleRepository.findByName(ERole.ROLE_MEDICO)
                                .orElseThrow(() -> new RuntimeException("Error: Role not found."));
                        roles.add(medicoRole);
                        isMedico = !isMedico;
                        break;}
                    case "ROLE_PACIENTE":{
                        Role pacienteRole = roleRepository.findByName(ERole.ROLE_PACIENTE)
                                .orElseThrow(() -> new RuntimeException("Error: Role not found."));
                        roles.add(pacienteRole);
                        isPaciente = !isPaciente;
                        break;}
                    default:
                        throw new RuntimeException("Error: Invalid role.");
                }
            }
            user.setRoles(roles);
        }

        // Guardar el nuevo usuario
        User savedUser = userRepository.save(user);
        if (isMedico) {
            // Crear un nuevo Medico con los valores de ci, sexo, telefono como null
            Medico newMedico = new Medico(null, null, null, savedUser);
            medicoRepository.save(newMedico);
        }
        if (isPaciente) {
            // Crear un nuevo Medico con los valores de ci, sexo, telefono como null
            Paciente newPaciente = new Paciente(null, null, null,null,null, savedUser);
            pacienteRepository.save(newPaciente);
        }
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }




    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        // Buscar al usuario por ID
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

        user.setPassword("");

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("password",user.getPassword());
        response.put("email", user.getEmail());

        // Obtener los roles del usuario y convertirlos a una lista de strings
        List<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName().toString())  // Convertir el rol a string
                .collect(Collectors.toList());
        response.put("roles", roleNames);

        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest updateUserRequest) {


        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        Set<String> roles = updateUserRequest.getRoles();

        if (updateUserRequest.getUsername() != null && !updateUserRequest.getUsername().isEmpty()) {
            user.setUsername(updateUserRequest.getUsername());
        }

        if (updateUserRequest.getEmail() != null && !updateUserRequest.getEmail().isEmpty()) {
            user.setEmail(updateUserRequest.getEmail());
        }

        if (updateUserRequest.getPassword() != null && !updateUserRequest.getPassword().isEmpty()) {
            String encodedPassword = encoder.encode(updateUserRequest.getPassword());
            user.setPassword(encodedPassword);
        }

        Set<Role> rolp = new HashSet<>();

        if (roles==null || roles.isEmpty()) {
            Role userRole = roleRepository.findByName(ERole.ROLE_PACIENTE)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            rolp.add(userRole);
        } else {
            roles.forEach(role -> {
                switch (role) {
                    case "ROLE_ADMIN":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        rolp.add(adminRole);
                        break;
                    case "ROLE_MEDICO":
                        Role medicoRole = roleRepository.findByName(ERole.ROLE_MEDICO)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        rolp.add(medicoRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_PACIENTE)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        rolp.add(userRole);
                        break;
                }
            });
        }

        user.setRoles(rolp);

        User updatedUser = userRepository.save(user);

        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

        boolean isMedico = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals(ERole.ROLE_MEDICO));
        if (isMedico) {
            Medico medico = medicoRepository.findByUser(user)
                    .orElseThrow(() -> new ResourceNotFoundException("Medico not found for user with id " + id));
            medicoRepository.delete(medico);
        }

        boolean isPaciente = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals(ERole.ROLE_PACIENTE));
        if (isPaciente) {
            Paciente paciente = pacienteRepository.findByUser(user)
                    .orElseThrow(() -> new ResourceNotFoundException("Paciente not found for user with id " + id));
            pacienteRepository.delete(paciente);
        }

        userRepository.delete(user);

        return ResponseEntity.noContent().build();
    }

}

