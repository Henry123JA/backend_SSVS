
package com.bezkoder.spring.security.postgresql.security.services;

import com.bezkoder.spring.security.postgresql.models.FichaNueva;
import com.bezkoder.spring.security.postgresql.models.Paciente;
import com.bezkoder.spring.security.postgresql.repository.FichaNuevaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PacienteService {

    @Autowired
    private FichaNuevaRepository fichaNuevaRepository;


    public List<Paciente> getPacientesPorFichas(List<FichaNueva> fichas) {
        if (fichas == null || fichas.isEmpty()) {
            throw new IllegalArgumentException("La lista de fichas no puede estar vacía");
        }

        // Usaremos un Set para evitar duplicados de pacientes
        Set<Paciente> pacientesUnicos = new HashSet<>();

        for (FichaNueva ficha : fichas) {
            Paciente paciente = ficha.getPaciente();
            if (paciente != null) {
                pacientesUnicos.add(paciente);
            }
        }

        return new ArrayList<>(pacientesUnicos);
    }
}
