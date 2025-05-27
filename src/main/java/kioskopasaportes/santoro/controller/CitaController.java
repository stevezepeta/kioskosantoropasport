package kioskopasaportes.santoro.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kioskopasaportes.santoro.dto.CitaDTO;
import kioskopasaportes.santoro.service.CitaService;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private static final Logger logger = LoggerFactory.getLogger(CitaController.class);

    private final CitaService service;

    public CitaController(CitaService service) {
        this.service = service;
    }

    /* ---------- CREATE ---------- */
    @PostMapping
    public ResponseEntity<CitaDTO> crear(@Valid @RequestBody CitaDTO dto) {
        logger.info("Creando cita para ciudadano {}", dto.getCiudadanoIdExterno());
        CitaDTO creada = service.crear(dto);
        return ResponseEntity
                .created(URI.create("/api/citas/" + creada.getId()))
                .body(creada);
    }

    /* ---------- READ ---------- */
    @GetMapping(params = "fecha")
    public List<CitaDTO> listarPorFecha(@RequestParam LocalDate fecha) {
        logger.debug("Listando citas por fecha {}", fecha);
        return service.listarPorFecha(fecha);
    }

    /* ---------- DELETE ---------- */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        logger.warn("Cancelando cita {}", id);
        service.cancelar(id);
        return ResponseEntity.noContent().build();
    }

    /* ---------- REFRESH opcional ---------- */
    @PostMapping("/refresh")
    public ResponseEntity<String> refreshCache() {
        // lógica opcional de refresco
        logger.info("Refrescando cache de citas");
        return ResponseEntity.ok("Citas refrescadas");
    }
}
