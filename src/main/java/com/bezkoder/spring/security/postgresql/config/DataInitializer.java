package com.bezkoder.spring.security.postgresql.config;

import com.bezkoder.spring.security.postgresql.models.*;
import com.bezkoder.spring.security.postgresql.repository.*;
import com.bezkoder.spring.security.postgresql.security.services.HorarioService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.*;

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
    PacienteRepository pacienteRepository;

    @Autowired
    EspecialidadRepository especialidadRepository;

    @Autowired
    FichaNuevaRepository fichaNuevaRepository;

    @Autowired
    HorarioRepository horarioRepository;

    @Autowired
    BloqueHorarioRepository bloqueHorarioRepository;

    @Autowired
    AntecedenteRepository antecedenteRepository;

    @Autowired
    ConsultaRepository consultaRepository;

    @Autowired
    RecetaRepository recetaRepository;

    @Autowired
    ExamenRepository examenRepository;

    @Autowired
    TratamientoRepository tratamientoRepository;

    @Autowired
    HorarioService horarioService;

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

            createMedico("medico1","medico1@medico1.com","password","896060","M","77906781");
            createMedico("medico2","medico2@medico2.com","password","396061","F","77906711");

            createPaciente("paciente1","paciente1@paciente1.com","password","896062","M","67906712","2015-12-12","1000");
            createPaciente("paciente2","paciente2@paciente2.com","password","396063","O","67906713","2010-06-01","1001");
        }
        if (especialidadRepository.count() == 0) {
            createEspecialidad("Cardiología", "Especialidad médica que se ocupa de las enfermedades del corazón y del sistema circulatorio.");
            createEspecialidad("Neurología", "Especialidad que se dedica al diagnóstico y tratamiento de los trastornos del sistema nervioso.");
            createEspecialidad("Pediatría", "Rama de la medicina dedicada al cuidado de los niños y adolescentes.");
            createEspecialidad("Dermatología", "Especialidad que trata las enfermedades de la piel, cabello y uñas.");
            createEspecialidad("Oncologia", "Especialidad medica que se ocupa del diagnostico y tratamiento del cáncer.");
        }
        assignEspecialidadesToMedicos();

        if(horarioRepository.count()==0) {
            createHorario(1L, "Martes", LocalTime.of(10, 0), LocalTime.of(11, 30), 15, "activo");
            createHorario(1L, "Jueves", LocalTime.of(10, 0), LocalTime.of(11, 30), 15, "activo");
            createHorario(2L, "Lunes", LocalTime.of(9, 0), LocalTime.of(10, 0), 10, "activo");
            createHorario(2L, "Miercoles", LocalTime.of(9, 0), LocalTime.of(10, 0), 10, "activo");
        }

        if(fichaNuevaRepository.count()==0){
            crearFichasParaMedico();
            crearFichaNuevaParaPaciente2();
        }

        if(antecedenteRepository.count()==0){
            crearAntecedentesPaciente();
        }
        if (consultaRepository.count() == 0) {
            // Consultas para la ficha con id 1
            FichaNueva ficha1 = fichaNuevaRepository.findById(1L)
                    .orElseThrow(() -> new RuntimeException("Ficha con id 1 no encontrada"));
            Consulta consulta1 = crearConsulta(ficha1, "Dolor en el pecho", "Posible infarto", "Analítica y ECG", "El paciente fue referido para más pruebas");

            // Consultas para la ficha con id 2
            FichaNueva ficha2 = fichaNuevaRepository.findById(2L)
                    .orElseThrow(() -> new RuntimeException("Ficha con id 2 no encontrada"));
            Consulta consulta2 = crearConsulta(ficha2, "Dolor de cabeza persistente", "Migraña", "Reposo y analgésicos", "El paciente debe realizar seguimiento si los síntomas persisten");
            //----------------------para crear las recetas--------------------------------
            crearReceta(consulta1, "Aspirina 500mg", "Tomar 1 cada 8 horas durante 7 días");
            crearReceta(consulta2, "Ibuprofeno 400mg", "Tomar 1 cada 6 horas durante 5 días");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            //----------------------para crear los tratamientos--------------------------------
            crearTratamiento(consulta1,"Tratamiento para la Consulta 1: Posible infarto",
                        dateFormat.parse("2024-10-10"),
                        dateFormat.parse("2024-11-10"),
            "Diagnosticado con un posible infarto y requiere realizarse un seguimiento semanal para evaluar su respuesta al tratamiento.");


            crearTratamiento(consulta2,"Tratamiento para la Consulta 2: Migraña persistente",
                        dateFormat.parse("2024-12-05"),
                        dateFormat.parse("2025-1-10"),
            "Se recomienda reposo absoluto y medicación analgésica para el alivio del dolor. Se debe realizar un seguimiento de los síntomas, especialmente si la migraña persiste o se intensifica, para determinar si se requiere un cambio en el tratamiento.");

            //----------------------para crear los examenes--------------------------------
            // Crear los exámenes para la consulta 1
            Examen examen1 = new Examen("Laboratorio Central", "Electrocardiograma (ECG)", "Normal", consulta1);
            examen1.setFechaExamen("2024-10-10");
            examenRepository.save(examen1);

            Examen examen2 = new Examen("Laboratorio Central", "Radiografía de Tórax", "Sin anomalías", consulta1);
            examen2.setFechaExamen("2024-10-11");
            examenRepository.save(examen2);

        }

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
                medico.setUser(user);
                medicoRepository.save(medico);
            }

            if (roles.contains(ERole.ROLE_PACIENTE)) {
                Paciente paciente = new Paciente();
                paciente.setUser(user);
                pacienteRepository.save(paciente);
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
    private void createPaciente(String username, String email, String password, String ci, String sexo,String telefono,String fechaNacimiento,String codigoAsegurado) {
        if (!userRepository.existsByUsername(username)) {
            User user = new User(username, email, passwordEncoder.encode(password));
            Set<Role> userRoles = new HashSet<>();

            Role userRole = roleRepository.findByName(ERole.ROLE_PACIENTE)
                    .orElseThrow(() -> new RuntimeException("Error: Role " + " not found."));
            userRoles.add(userRole);

            user.setRoles(userRoles);
            userRepository.save(user);

            Paciente paciente = new Paciente();
            paciente.setUser(user);
            paciente.setCi(ci);
            paciente.setSexo(sexo);
            paciente.setTelefono(telefono);
            paciente.setFechaNacimiento(fechaNacimiento);
            paciente.setCodigoAsegurado(codigoAsegurado);
            pacienteRepository.save(paciente);
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

    private void createHorario(Long medicoId, String diaSemana, LocalTime horaInicio, LocalTime horaFin, int duracion, String estado) {
        Optional<Medico> medicoOpt = medicoRepository.findById(medicoId);

        if (medicoOpt.isPresent()) {
            Medico medico = medicoOpt.get();
            Horario horario = new Horario(horaInicio, horaFin, duracion, diaSemana, estado, medico);
            Horario newHorario = horarioRepository.save(horario);
            horarioService.crearHorariosConBloques(newHorario);

        }
    }

    private void crearFichasParaMedico() {
        // Obtener al médico con ID 1
        Optional<Medico> medicoOpt = medicoRepository.findById(1L);
        if (!medicoOpt.isPresent()) {
            throw new RuntimeException("Error: Medico no encontrado con ID 1.");
        }

        Medico medico = medicoOpt.get();

        // Obtener las especialidades "Cardiología" y "Neurología"
        Optional<Especialidad> cardiologia = especialidadRepository.findByNombre("Cardiología");
        if (!cardiologia.isPresent()) {
            throw new RuntimeException("Error: Especialidad 'Cardiología' no encontrada.");
        }

        Optional<Especialidad> neurologia = especialidadRepository.findByNombre("Neurología");
        if (!neurologia.isPresent()) {
            throw new RuntimeException("Error: Especialidad 'Neurología' no encontrada.");
        }

        // Obtener los bloques de horario con ID 2 y 9
        Optional<BloqueHorario> bloque1 = bloqueHorarioRepository.findById(2L);
        if (!bloque1.isPresent()) {
            throw new RuntimeException("Error: BloqueHorario con ID 2 no encontrado.");
        }

        Optional<BloqueHorario> bloque2 = bloqueHorarioRepository.findById(9L);
        if (!bloque2.isPresent()) {
            throw new RuntimeException("Error: BloqueHorario con ID 9 no encontrado.");
        }

        // Marcar los bloques como "ocupados"
        BloqueHorario bloque1Actualizado = bloque1.get();
        bloque1Actualizado.setEstado("ocupado");
        bloque1Actualizado = bloqueHorarioRepository.save(bloque1Actualizado);

        BloqueHorario bloque2Actualizado = bloque2.get();
        bloque2Actualizado.setEstado("ocupado");
        bloque2Actualizado = bloqueHorarioRepository.save(bloque2Actualizado);

        // Obtener pacientes con ID 1 y 2
        Optional<Paciente> paciente1Opt = pacienteRepository.findById(1L);
        if (!paciente1Opt.isPresent()) {
            throw new RuntimeException("Error: Paciente con ID 1 no encontrado.");
        }
        Paciente paciente1 = paciente1Opt.get();

        Optional<Paciente> paciente2Opt = pacienteRepository.findById(2L);
        if (!paciente2Opt.isPresent()) {
            throw new RuntimeException("Error: Paciente con ID 2 no encontrado.");
        }
        Paciente paciente2 = paciente2Opt.get();

        // Crear las dos fichas
        crearFicha(medico, paciente1, cardiologia.get(), bloque1Actualizado);
        crearFicha(medico, paciente2, neurologia.get(), bloque2Actualizado);
    }

    private void crearFicha(Medico medico, Paciente paciente, Especialidad especialidad, BloqueHorario bloqueHorario) {
        // Crear la ficha para un paciente
        FichaNueva ficha = new FichaNueva();
        ficha.setPaciente(paciente);
        ficha.setMedicoId(medico.getId());
        ficha.setEspecialidad(especialidad);
        ficha.setBloqueHorario(bloqueHorario);
        ficha.setFecha(ficha.getFechaActualLaPaz()); // Fecha actual en La Paz
        ficha.setHora(ficha.getHoraActualLaPaz());   // Hora actual en La Paz
        ficha.setEstado("pendiente");

        // Guardar la ficha en la base de datos
        FichaNueva newFicha = fichaNuevaRepository.save(ficha);

        System.out.println("Ficha creada para el paciente: " + paciente.getId() + " con especialidad: " + especialidad.getNombre());
    }
    private void crearFichaNuevaParaPaciente2() {
        // Obtener al paciente con ID 2
        Optional<Paciente> paciente2Opt = pacienteRepository.findById(2L);
        if (!paciente2Opt.isPresent()) {
            throw new RuntimeException("Error: Paciente con ID 2 no encontrado.");
        }
        Paciente paciente2 = paciente2Opt.get();

        // Obtener al médico con ID 2
        Optional<Medico> medicoOpt = medicoRepository.findById(2L);
        if (!medicoOpt.isPresent()) {
            throw new RuntimeException("Error: Médico con ID 2 no encontrado.");
        }
        Medico medico = medicoOpt.get();

        // Obtener la especialidad "Dermatología"
        Optional<Especialidad> dermatologiaOpt = especialidadRepository.findByNombre("Dermatología");
        if (!dermatologiaOpt.isPresent()) {
            throw new RuntimeException("Error: Especialidad 'Dermatología' no encontrada.");
        }
        Especialidad dermatologia = dermatologiaOpt.get();

        // Obtener el bloque horario con ID 15
        Optional<BloqueHorario> bloqueHorarioOpt = bloqueHorarioRepository.findById(15L);
        if (!bloqueHorarioOpt.isPresent()) {
            throw new RuntimeException("Error: BloqueHorario con ID 15 no encontrado.");
        }
        BloqueHorario bloqueHorario = bloqueHorarioOpt.get();

        //BloqueHorario bloque1Actualizado = bloque1.get();
        bloqueHorario.setEstado("ocupado");
        BloqueHorario bloque1Actualizado = bloqueHorarioRepository.save(bloqueHorario);


        crearFicha(medico, paciente2, dermatologia, bloque1Actualizado);

    }


    private void crearAntecedentesPaciente() {
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(1L);
        if (!pacienteOpt.isPresent()) {
            throw new RuntimeException("Error: Paciente con ID 1 no encontrado.");
        }

        Paciente paciente = pacienteOpt.get();

        // Crear un objeto Antecedente
        Antecedente antecedente = new Antecedente();
        antecedente.setPaciente(paciente);

        // Ejemplos de enfermedades base
        String enfermedadesBase = "Hipertensión, Diabetes tipo 2, Asma";
        antecedente.setEnfermedadesBase(enfermedadesBase);

        // Ejemplos de enfermedades hereditarias
        String enfermedadesHereditarias = "Cáncer de mama, Hipotiroidismo";
        antecedente.setEnfermedadesHereditarias(enfermedadesHereditarias);

        // Incluir medicamentos en "otras"
        String otras = "Medicamentos: Metformina (para diabetes), Enalapril (para hipertensión), Salbutamol (para asma)";
        antecedente.setOtras(otras);

        // Guardar el antecedente en la base de datos
        antecedenteRepository.save(antecedente);
        System.out.println("Antecedente creado para el paciente con ID " + paciente.getId());
    }

    private Consulta crearConsulta(FichaNueva ficha, String sintomas, String diagnostico, String tratamiento, String notas) {
        Consulta consulta = new Consulta();
        ficha.setEstado("atendido");
        FichaNueva newFicha = fichaNuevaRepository.save(ficha);
        consulta.setFicha(newFicha);
        consulta.setSintomas(sintomas);
        consulta.setDiagnostico(diagnostico);
        consulta.setTratamiento(tratamiento);
        consulta.setNotas(notas);

        // La fecha de consulta se establece automáticamente con @PrePersist
        consultaRepository.save(consulta);

        return consulta;
    }

    private void crearReceta(Consulta consulta, String medicamentos, String indicaciones) {
        Receta receta = new Receta();
        receta.setMedicamentos(medicamentos);
        receta.setIndicaciones(indicaciones);
        receta.setConsulta(consulta);

        // La fecha de la receta se establece automáticamente con @PrePersist
        recetaRepository.save(receta);

    }

    private void crearTratamiento(Consulta consulta, String descripcion, Date fechaInicio, Date fechaFin, String seguimiento) {
        Tratamiento tratamiento = new Tratamiento();
        tratamiento.setDescripcion(descripcion);
        tratamiento.setFechaInicio(fechaInicio);
        tratamiento.setFechaFin(fechaFin);
        tratamiento.setSeguimiento(seguimiento);
        tratamiento.setConsulta(consulta);

        // La fecha de la receta se establece automáticamente con @PrePersist
        tratamientoRepository.save(tratamiento);
    }
}
