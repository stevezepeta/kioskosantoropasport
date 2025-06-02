package kioskopasaportes.santoro.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kioskopasaportes.santoro.model.Pasaporte;
import kioskopasaportes.santoro.model.dto.PasaporteRequestDTO;
import kioskopasaportes.santoro.service.PasaporteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/pasaportes")
@RequiredArgsConstructor
@Slf4j
public class PasaporteController {

    private final PasaporteService pasaporteService;

    @PostMapping("/add")
    public ResponseEntity<?> addPasaporte(@RequestBody PasaporteRequestDTO dto) {
        try {
            Pasaporte saved = pasaporteService.registrarPasaporte(dto);
            log.info("Pasaporte registrado para CURP={}, Numero={}", saved.getPersona().getCurp(), saved.getNumeroPasaporte());
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}

