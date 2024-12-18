package com.bezkoder.spring.security.postgresql.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = {"http://localhost:4200","*"})
@RestController
@RequestMapping("/api/test")
public class TestController {
  @GetMapping("/all")
  public String allAccess() {
    return "Public Content.TODOS";
  }

  @GetMapping("/paciente")
  @PreAuthorize("hasRole('PACIENTE') or hasRole('ADMIN')")
  public String userAccess() {
    return "Pacienter Content. DE PACIENTE...";
  }

  @GetMapping("/medico")
  @PreAuthorize("hasRole('MEDICO')")
  public String moderatorAccess() {
    return "Medico Board...DE MEDICO";
  }

  @GetMapping("/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public String adminAccess() {
    return "Admin Board...DE ADMIN";
  }
}
