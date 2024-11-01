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
            Role roleUser = new Role(ERole.ROLE_USER);
            Role roleModerator = new Role(ERole.ROLE_MODERATOR);
            Role roleAdmin = new Role(ERole.ROLE_ADMIN);

            roleRepository.save(roleUser);
            roleRepository.save(roleModerator);
            roleRepository.save(roleAdmin);
        }

        // Insert users if not already present
        if (userRepository.count() == 0) {
            createUser("user", "user@user.com", "password", ERole.ROLE_USER);
            createUser("medico", "medico@medico.com", "password", ERole.ROLE_MODERATOR);
            createUser("admin", "admin@admin.com", "password", ERole.ROLE_ADMIN);
        }
    }

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
    }
}
