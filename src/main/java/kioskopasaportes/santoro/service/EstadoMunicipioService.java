package kioskopasaportes.santoro.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import kioskopasaportes.santoro.dto.EstadoDTO;
import kioskopasaportes.santoro.dto.EstadoMunicipioDTO;
import kioskopasaportes.santoro.dto.MunicipioDTO;

@Service
public class EstadoMunicipioService {

    private final ObjectMapper mapper = new ObjectMapper();
    private List<EstadoMunicipioDTO> catalogo;

    @PostConstruct
    void init() throws IOException {
        // Carga el JSON una sola vez al arrancar
        var file = new ClassPathResource("data/estados-municipios.json").getInputStream();
        catalogo = mapper.readValue(file, new TypeReference<>() {});
    }

    /**
     * Devuelve solo los estados (id + nombre).
     */
    public List<EstadoDTO> getAllEstados() {
        return catalogo.stream()
                       .map(e -> new EstadoDTO(e.getId(), e.getNombre()))
                       .collect(Collectors.toList());
    }

    /**
     * Devuelve los municipios de un estado concreto (por su id).
     */
    public List<MunicipioDTO> getMunicipiosPorEstado(Integer idEstado) {
        return catalogo.stream()
                       .filter(e -> e.getId().equals(idEstado))
                       .findFirst()
                       .map(EstadoMunicipioDTO::getMunicipios)
                       .orElse(List.of())
                       .stream()
                       .map(m -> new MunicipioDTO(m.getId(), m.getNombre()))
                       .collect(Collectors.toList());
    }
}
