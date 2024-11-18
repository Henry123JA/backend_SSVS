package com.bezkoder.spring.security.postgresql.security.services;

import com.bezkoder.spring.security.postgresql.models.Paciente;
import com.bezkoder.spring.security.postgresql.payload.request.UpdatePacienteRequest;
import com.bezkoder.spring.security.postgresql.payload.dto.PacienteDTO;
import com.bezkoder.spring.security.postgresql.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    /**
     * Obtiene un paciente por su ID
     * @param id El ID del paciente
     * @return Un objeto PacienteDTO que representa al paciente
     */
    public PacienteDTO getPaciente(Long id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente not found with id " + id));

        return new PacienteDTO(paciente);
    }

    /**
     * Actualiza la información de un paciente
     * @param id El ID del paciente a actualizar
     * @param updatePacienteRequest El objeto con los nuevos datos
     * @return El paciente actualizado
     */
    public Paciente updatePaciente(Long id, UpdatePacienteRequest updatePacienteRequest) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente not found with id " + id));

        // Actualizar los campos del paciente con los datos del request
        paciente.setCi(updatePacienteRequest.getCi());
        paciente.setSexo(updatePacienteRequest.getSexo());
        paciente.setTelefono(updatePacienteRequest.getTelefono());
        // Los campos direccion y fechaNacimiento ya no se actualizan

        // Guardar los cambios en la base de datos
        return pacienteRepository.save(paciente);
    }

    /**
     * Elimina un paciente por su ID
     * @param id El ID del paciente a eliminar
     */
    public void deletePaciente(Long id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente not found with id " + id));

        pacienteRepository.delete(paciente);
    }
}