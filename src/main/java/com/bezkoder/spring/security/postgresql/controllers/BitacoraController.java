package com.bezkoder.spring.security.postgresql.controllers;

import com.bezkoder.spring.security.postgresql.models.Bitacora;
import com.bezkoder.spring.security.postgresql.models.User;
import com.bezkoder.spring.security.postgresql.repository.BitacoraRepository;
import com.bezkoder.spring.security.postgresql.repository.UserRepository;
import com.bezkoder.spring.security.postgresql.payload.request.BitacoraRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = {"http://localhost:4200","*"})
@RestController
@RequestMapping("/api/bitacoras")
public class BitacoraController {

    @Autowired
    private BitacoraRepository bitacoraRepository;

    @Autowired
    private UserRepository userRepository;

    // Obtener todas las bitácoras de un usuario
    @GetMapping("/usuario/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Bitacora> getBitacorasByUser(@PathVariable Long userId) {
        return bitacoraRepository.findByUserId(userId);
    }

    // Registrar una nueva bitácora
    @PostMapping("/registro")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Bitacora> registrarBitacora(@RequestBody BitacoraRequest  bitacoraRequest) {
        Optional<User> user = userRepository.findById(bitacoraRequest.getUserId());
        /*
        System.out.println("de tipoaccion: "+bitacoraRequest.getTipoAccion());
        System.out.println("de descripcionAccion: "+bitacoraRequest.getDescripcionAccion());
        System.out.println("de nombre: "+user.get().getUsername());
        System.out.println("de ip: "+bitacoraRequest.getIp());
        */
        ZonedDateTime fechaHora = ZonedDateTime.now(ZoneId.of("America/La_Paz"));
        //System.out.println("de fecha-hora (original): " + fechaHora);

        // Si necesitas formatear la fecha antes de enviarla:
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String fechaHoraStr = fechaHora.format(formatter);
        //System.out.println("de fecha-hora (formateada): " + fechaHoraStr);
        if (!user.isPresent()) {
            return ResponseEntity.badRequest().body(null); // Usuario no encontrado
        }

        Bitacora bitacora = new Bitacora(
                bitacoraRequest.getTipoAccion(),
                bitacoraRequest.getDescripcionAccion(),
                bitacoraRequest.getIp(),
                fechaHoraStr,  // Fecha y hora actual
                user.get()  // Asignar el usuario a la bitácora
        );

        // Guardar la bitácora
        Bitacora savedBitacora = bitacoraRepository.save(bitacora);

        // Retornar la bitácora registrada
        return ResponseEntity.ok(savedBitacora);
    }

}

