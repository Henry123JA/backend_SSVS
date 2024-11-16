package com.bezkoder.spring.security.postgresql.security.services;


//package com.bezkoder.spring.security.postgresql.services;

import com.bezkoder.spring.security.postgresql.models.*;
import com.bezkoder.spring.security.postgresql.payload.dto.*;
import com.bezkoder.spring.security.postgresql.payload.request.UpdateMedicoRequest;
import com.bezkoder.spring.security.postgresql.repository.MedicoRepository;
import com.bezkoder.spring.security.postgresql.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Hibernate;

import java.util.Optional;

@Service
public class MedicoService {

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Transactional
    public Medico updateMedico(Long medicoId, UpdateMedicoRequest updateMedicoRequest) {
        // Buscar el médico por su ID
        System.out.println("entro al servicio...");
        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new IllegalArgumentException("Medico no encontrado con ID: " + medicoId));

        User user = userRepository.findById(updateMedicoRequest.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + updateMedicoRequest.getUser().getId()));

        // Actualizar los datos del médico (solo si no son null o vacíos)
        if (updateMedicoRequest.getCi() != null && !updateMedicoRequest.getCi().isEmpty()) {
            medico.setCi(updateMedicoRequest.getCi());
        }
        if (updateMedicoRequest.getSexo() != null && !updateMedicoRequest.getSexo().isEmpty()) {
            medico.setSexo(updateMedicoRequest.getSexo());
        }
        if (updateMedicoRequest.getTelefono() != null && !updateMedicoRequest.getTelefono().isEmpty()) {
            medico.setTelefono(updateMedicoRequest.getTelefono());
        }

        // Actualizar los datos del usuario (solo si no son null o vacíos)
        if (updateMedicoRequest.getUser().getUsername() != null && !updateMedicoRequest.getUser().getUsername().isEmpty()) {
            user.setUsername(updateMedicoRequest.getUser().getUsername());
        }
        if (updateMedicoRequest.getUser().getEmail() != null && !updateMedicoRequest.getUser().getEmail().isEmpty()) {
            user.setEmail(updateMedicoRequest.getUser().getEmail());
        }

        // Actualizar la contraseña solo si no es null ni vacía
        if (updateMedicoRequest.getUser().getPassword() != null && !updateMedicoRequest.getUser().getPassword().isEmpty()) {
            // Cifrar la nueva contraseña
            String encodedPassword = encoder.encode(updateMedicoRequest.getUser().getPassword());
            user.setPassword(encodedPassword);
        }
        //Hibernate.initialize(medico.getUser());
        // Guardar los cambios en el usuario
        userRepository.save(user);

        // Guardar los cambios en el médico

        System.out.println("salio del servicio...");
        return medicoRepository.save(medico);
    }


    @Transactional
    public MedicoDTO getMedico(Long id) {
        Medico medico = medicoRepository.findById(id).orElseThrow(() -> new RuntimeException("Medico no encontrado"));
        Hibernate.initialize(medico.getUser());
        return new MedicoDTO(medico);
    }

}
