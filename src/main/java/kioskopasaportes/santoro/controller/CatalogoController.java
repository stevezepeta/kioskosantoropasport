// src/main/java/kioskopasaportes/santoro/controller/CatalogoController.java
package kioskopasaportes.santoro.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kioskopasaportes.santoro.dto.EstadoDTO;
import kioskopasaportes.santoro.dto.MunicipioDTO;
import kioskopasaportes.santoro.dto.OficinaDTO;
import kioskopasaportes.santoro.service.EstadoMunicipioService;
import kioskopasaportes.santoro.service.OficinaService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CatalogoController {

    private final EstadoMunicipioService estadoMunicipioService;
    private final OficinaService oficinaService;

    @GetMapping("/estados")
    public ResponseEntity<List<EstadoDTO>> listarEstados() {
        return ResponseEntity.ok(estadoMunicipioService.getAllEstados());
    }

    @GetMapping("/estados/{id}/municipios")
    public ResponseEntity<List<MunicipioDTO>> listarMunicipios(
            @PathVariable("id") Integer idEstado) {

        var lst = estadoMunicipioService.getMunicipiosPorEstado(idEstado);
        if (lst.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(lst);
    }

    @GetMapping("/estados/{id}/oficinas")
    public ResponseEntity<List<OficinaDTO>> listarOficinas(
            @PathVariable("id") Integer idEstado) {

        var oficinas = oficinaService.getOficinasPorEstado(idEstado);
        if (oficinas.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(oficinas);
    }
}
