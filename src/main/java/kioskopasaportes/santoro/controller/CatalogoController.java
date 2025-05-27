package kioskopasaportes.santoro.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kioskopasaportes.santoro.dto.EstadoDTO;
import kioskopasaportes.santoro.dto.MunicipioDTO;
import kioskopasaportes.santoro.service.EstadoMunicipioService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CatalogoController {

    private final EstadoMunicipioService estadoMunicipioService;

    /**
     * Devuelve todos los estados (sin municipios).
     */
   @GetMapping("/estados")
public ResponseEntity<List<EstadoDTO>> listarEstados() {
    return ResponseEntity.ok(estadoMunicipioService.getAllEstados());
}

@GetMapping("/estados/{id}/municipios")
public ResponseEntity<List<MunicipioDTO>> listarMunicipios(@PathVariable Integer id) {
    var ms = estadoMunicipioService.getMunicipiosPorEstado(id);
    if (ms.isEmpty()) return ResponseEntity.notFound().build();
    return ResponseEntity.ok(ms);
}

}
