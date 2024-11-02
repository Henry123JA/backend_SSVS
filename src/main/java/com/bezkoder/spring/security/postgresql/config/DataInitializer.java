package com.bezkoder.spring.security.postgresql.config;

import com.bezkoder.spring.security.postgresql.models.ERole;
import com.bezkoder.spring.security.postgresql.models.Role;
import com.bezkoder.spring.security.postgresql.models.User;
import com.bezkoder.spring.security.postgresql.repository.RoleRepository;
import com.bezkoder.spring.security.postgresql.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Insert roles if not already present
        if (roleRepository.count() == 0) {
            Role roleUser = new Role(ERole.ROLE_PACIENTE);
            Role roleModerator = new Role(ERole.ROLE_MEDICO);
            Role roleAdmin = new Role(ERole.ROLE_ADMIN);

            roleRepository.save(roleUser);
            roleRepository.save(roleModerator);
            roleRepository.save(roleAdmin);
        }

        // Insert users if not already present
        /*if (userRepository.count() == 0) {
            createUser("paciente", "paciente@paciente.com", "password", ERole.ROLE_PACIENTE);
            createUser("medico", "medico@medico.com", "password", ERole.ROLE_MEDICO);
            createUser("admin", "admin@admin.com", "password", ERole.ROLE_ADMIN);
        }*/
        if(userRepository.count() == 0){
            Set<ERole> rolesVariosPaciente = new HashSet<>();
            rolesVariosPaciente.add(ERole.ROLE_PACIENTE);

            Set<ERole> rolesVariosMedico = new HashSet<>();
            rolesVariosMedico.add(ERole.ROLE_MEDICO);

            Set<ERole> rolesVariosAdmin = new HashSet<>();
            rolesVariosAdmin.add(ERole.ROLE_ADMIN);
            rolesVariosAdmin.add(ERole.ROLE_MEDICO);
            rolesVariosAdmin.add(ERole.ROLE_PACIENTE);

            createUser("paciente","paciente@paciente.com","password",rolesVariosPaciente);
            createUser("medico","medico@medico.com","password",rolesVariosMedico);
            createUser("admin","admin@admin.com","password",rolesVariosAdmin);
        }
    }
/*
    private void createUser(String username, String email, String password, ERole role) {
        if (!userRepository.existsByUsername(username)) {
            User user = new User(username, email, passwordEncoder.encode(password));
            Set<Role> roles = new HashSet<>();
            Role userRole = roleRepository.findByName(role)
                    .orElseThrow(() -> new RuntimeException("Error: Role not found."));
            roles.add(userRole);
            user.setRoles(roles);
            userRepository.save(user);
        }
    }*/
    private void createUser(String username, String email, String password, Set<ERole> roles) {
        if (!userRepository.existsByUsername(username)) {
            User user = new User(username, email, passwordEncoder.encode(password));
            Set<Role> userRoles = new HashSet<>();

            for (ERole role : roles) {
                Role userRole = roleRepository.findByName(role)
                        .orElseThrow(() -> new RuntimeException("Error: Role " + role + " not found."));
                userRoles.add(userRole);
            }

            user.setRoles(userRoles);
            userRepository.save(user);
        }
    }

}
