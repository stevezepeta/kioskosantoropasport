package kioskopasaportes.santoro.controller;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kioskopasaportes.santoro.dto.OficinaDTO;
import kioskopasaportes.santoro.service.OficinaService;

@RestController
@RequestMapping("/api/oficinas")
public class OficinaController {

    private static final Logger logger = LoggerFactory.getLogger(OficinaController.class);

    private final OficinaService service;

    public OficinaController(OficinaService service) {
        this.service = service;
    }

    /* ---------- CREATE ---------- */
    @PostMapping
    public ResponseEntity<OficinaDTO> crear(@Valid @RequestBody OficinaDTO dto) {
        logger.info("Creando oficina {}", dto.getNombre());
        OficinaDTO creada = service.create(dto);
        return ResponseEntity
                .created(URI.create("/api/oficinas/" + creada.getId()))
                .body(creada);
    }

    /* ---------- READ ---------- */
    @GetMapping("/{id}")
    public OficinaDTO detalle(@PathVariable Long id) {
        logger.debug("Detalle oficina {}", id);
        return service.findById(id);
    }

    @GetMapping
    public List<OficinaDTO> listar() {
        logger.debug("Listando oficinas");
        return service.findAll();
    }

    /* ---------- UPDATE ---------- */
    @PutMapping("/{id}")
    public OficinaDTO actualizar(@PathVariable Long id,
                                 @Valid @RequestBody OficinaDTO dto) {
        logger.info("Actualizando oficina {}", id);
        return service.update(id, dto);
    }

    /* ---------- DELETE ---------- */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        logger.warn("Eliminando oficina {}", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /* ---------- REFRESH opcional ---------- */
    @PostMapping("/refresh")
    public ResponseEntity<String> refreshCache() {
        // lógica opcional de refresco
        logger.info("Refrescando cache de oficinas");
        return ResponseEntity.ok("Oficinas refrescadas");
    }
}
