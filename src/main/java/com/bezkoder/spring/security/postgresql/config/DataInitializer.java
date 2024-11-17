package com.bezkoder.spring.security.postgresql.config;

import com.bezkoder.spring.security.postgresql.models.*;
import com.bezkoder.spring.security.postgresql.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Transactional
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MedicoRepository medicoRepository;

    @Autowired
    EspecialidadRepository especialidadRepository;

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

        if(userRepository.count() == 0){
            Set<ERole> rolesVariosPaciente = new HashSet<>();
            rolesVariosPaciente.add(ERole.ROLE_PACIENTE);

            Set<ERole> rolesVariosMedico = new HashSet<>();
            rolesVariosMedico.add(ERole.ROLE_MEDICO);

            Set<ERole> rolesVariosAdmin = new HashSet<>();
            rolesVariosAdmin.add(ERole.ROLE_ADMIN);
            //rolesVariosAdmin.add(ERole.ROLE_MEDICO);
            //rolesVariosAdmin.add(ERole.ROLE_PACIENTE);

            createUser("paciente","paciente@paciente.com","password",rolesVariosPaciente);
            createUser("medico","medico@medico.com","password",rolesVariosMedico);
            createUser("admin","admin@admin.com","password",rolesVariosAdmin);

            createMedico("medico1","medico1@medico1.com","password","8960407","M","77906781");
            createMedico("medico2","medico2@medico2.com","password","3960306","F","77906711");
        }
        if (especialidadRepository.count() == 0) {
            createEspecialidad("Cardiología", "Especialidad médica que se ocupa de las enfermedades del corazón y del sistema circulatorio.");
            createEspecialidad("Neurología", "Especialidad que se dedica al diagnóstico y tratamiento de los trastornos del sistema nervioso.");
            createEspecialidad("Pediatría", "Rama de la medicina dedicada al cuidado de los niños y adolescentes.");
            createEspecialidad("Dermatología", "Especialidad que trata las enfermedades de la piel, cabello y uñas.");
            createEspecialidad("Oncología", "Especialidad médica que se ocupa del diagnóstico y tratamiento del cáncer.");
        }
        assignEspecialidadesToMedicos();

        System.out.println("-------------DATAINITIALIZER CREADO EXITOSAMENTE----------");
    }

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

            // Si el usuario tiene el rol MEDICO, crear un registro en la tabla 'medicos'
            if (roles.contains(ERole.ROLE_MEDICO)) {
                Medico medico = new Medico();
                medico.setUser(user);  // Establecemos la relación con el usuario
                // Los demás campos como ci, sexo y telefono se dejan como null por defecto
                medicoRepository.save(medico);
            }
        }
    }

    private void createMedico(String username, String email, String password,String ci,String sexo,String telefono) {
        if (!userRepository.existsByUsername(username)) {
            User user = new User(username, email, passwordEncoder.encode(password));
            Set<Role> userRoles = new HashSet<>();

                Role userRole = roleRepository.findByName(ERole.ROLE_MEDICO)
                        .orElseThrow(() -> new RuntimeException("Error: Role " + " not found."));
                userRoles.add(userRole);

            user.setRoles(userRoles);
            userRepository.save(user);

            Medico medico = new Medico();
            medico.setUser(user);  // Establecemos la relación con el usuario
            medico.setCi(ci);
            medico.setSexo(sexo);
            medico.setTelefono(telefono);
            medicoRepository.save(medico);
        }
    }

    private void createEspecialidad(String nombre, String descripcion) {
        // Verificamos si la especialidad ya existe
        Optional<Especialidad> especialidadOpt = especialidadRepository.findByNombre(nombre);
        if (especialidadOpt.isEmpty()) {
            Especialidad especialidad = new Especialidad();
            especialidad.setNombre(nombre);
            especialidad.setDescripcion(descripcion);
            especialidadRepository.save(especialidad);
        }
    }

    @Transactional
    private void assignEspecialidadesToMedicos() {
        // Asignar especialidades a médicos para la inicialización
        Optional<Medico> medicoOpt = medicoRepository.findById(1L);
        //System.out.println("Medico encontrado: " + medicoOpt.isPresent());  // Verifica si el médico fue encontrado

        // Buscar las especialidades por nombre
        Optional<Especialidad> cardiologia = especialidadRepository.findByNombre("Cardiología");
        //System.out.println("Cardiología encontrada: " + cardiologia.isPresent());  // Verifica si se encontró Cardiología

        Optional<Especialidad> neurologia = especialidadRepository.findByNombre("Neurología");
        //System.out.println("Neurología encontrada: " + neurologia.isPresent());  // Verifica si se encontró Neurología

        // Si tanto el médico como las especialidades existen, proceder con la asignación
        if (medicoOpt.isPresent() && cardiologia.isPresent() && neurologia.isPresent()) {
            Medico medico = medicoOpt.get();  // Obtener el médico
            Especialidad cardiologiaEspecialidad = cardiologia.get();  // Obtener la especialidad de Cardiología
            Especialidad neurologiaEspecialidad = neurologia.get();  // Obtener la especialidad de Neurología

            medico.addEspecialidad(cardiologiaEspecialidad);
            medico.addEspecialidad(neurologiaEspecialidad);

            medicoRepository.save(medico);
            //System.out.println("Médico guardado correctamente con las especialidades."+medico.getEspecialidades().toString());
        }

        medicoOpt = medicoRepository.findById(2L); // Buscar el primer médico por ID
        cardiologia = especialidadRepository.findByNombre("Dermatología");
        neurologia = especialidadRepository.findByNombre("Neurología");


        if (medicoOpt.isPresent() && cardiologia.isPresent() && neurologia.isPresent()) {
            Medico medico1 = medicoOpt.get();
            Especialidad cardiologiaEspecialidad = cardiologia.get();
            Especialidad neurologiaEspecialidad = neurologia.get();

            // Asociamos las especialidades con el médico
            medico1.addEspecialidad(cardiologiaEspecialidad);
            medico1.addEspecialidad(neurologiaEspecialidad);

            medicoRepository.save(medico1);
        }

        // Asignar más especialidades a otros médicos de la misma forma si es necesario
        medicoOpt = medicoRepository.findById(3L); // Buscar el segundo médico
        Optional<Especialidad> pediatria = especialidadRepository.findByNombre("Pediatría");

        if (medicoOpt.isPresent() && pediatria.isPresent()) {
            Medico medico2 = medicoOpt.get();
            Especialidad pediatriaEspecialidad = pediatria.get();

            // Asociamos la especialidad con el médico
            medico2.addEspecialidad(pediatriaEspecialidad);
            medicoRepository.save(medico2);
        }
    }

}
