
package com.bezkoder.spring.security.postgresql.controllers;

import java.util.*;
import java.util.stream.Collectors;

import com.bezkoder.spring.security.postgresql.exceptions.ResourceNotFoundException;
//import com.bezkoder.spring.security.postgresql.models.User;
import com.bezkoder.spring.security.postgresql.payload.response.MessageResponse;
import com.bezkoder.spring.security.postgresql.repository.RoleRepository;
import com.bezkoder.spring.security.postgresql.repository.UserRepository;
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


//@CrossOrigin(origins = "http://localhost:4200")

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

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
            Role defaultRole = roleRepository.findByName(ERole.ROLE_PACIENTE)
                    .orElseThrow(() -> new RuntimeException("Error: Role not found."));
            user.setRoles(new HashSet<>(Collections.singletonList(defaultRole)));
        } else {
            // Convertir los roles en strings a objetos Role
            Set<Role> roles = new HashSet<>();
            for (String roleName : createUserRequest.getRoles()) {
                switch (roleName) {
                    case "ROLE_ADMIN":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role not found."));
                        roles.add(adminRole);
                        break;
                    case "ROLE_MEDICO":
                        Role medicoRole = roleRepository.findByName(ERole.ROLE_MEDICO)
                                .orElseThrow(() -> new RuntimeException("Error: Role not found."));
                        roles.add(medicoRole);
                        break;
                    case "ROLE_PACIENTE":
                        Role pacienteRole = roleRepository.findByName(ERole.ROLE_PACIENTE)
                                .orElseThrow(() -> new RuntimeException("Error: Role not found."));
                        roles.add(pacienteRole);
                        break;
                    default:
                        throw new RuntimeException("Error: Invalid role.");
                }
            }
            user.setRoles(roles);
        }

        // Guardar el nuevo usuario
        User savedUser = userRepository.save(user);

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

        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }
}

