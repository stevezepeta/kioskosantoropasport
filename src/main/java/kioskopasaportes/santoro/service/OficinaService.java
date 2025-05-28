// src/main/java/kioskopasaportes/santoro/service/OficinaService.java
package kioskopasaportes.santoro.service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import kioskopasaportes.santoro.dto.EstadoOficinasDTO;
import kioskopasaportes.santoro.dto.OficinaDTO;

@Service
public class OficinaService {

    private final ObjectMapper mapper = new ObjectMapper();
    private List<EstadoOficinasDTO> catalogo = Collections.emptyList();

    @PostConstruct
    public void init() throws IOException {
        var resource = new ClassPathResource("data/estados-oficinas.json");
        try (var in = resource.getInputStream()) {
            catalogo = mapper.readValue(in,
                new TypeReference<List<EstadoOficinasDTO>>() {});
        }
    }

    /** Devuelve todos los estados con su lista de oficinas */
    public List<EstadoOficinasDTO> getEstadosConOficinas() {
        return catalogo;
    }

    /** Devuelve sólo las oficinas de un estado concreto */
    public List<OficinaDTO> getOficinasPorEstado(Integer idEstado) {
        return catalogo.stream()
                       .filter(e -> e.getId().equals(idEstado))
                       .findFirst()
                       .map(EstadoOficinasDTO::getOficinas)
                       .orElse(Collections.emptyList());
    }
}
