package com.clinica.abc.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BotController {

  @GetMapping("/bot")
  public ResponseEntity<String> getBot() {
    return ResponseEntity.ok("Bot ok");
  }
}
