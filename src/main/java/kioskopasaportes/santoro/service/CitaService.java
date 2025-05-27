package kioskopasaportes.santoro.service;

import kioskopasaportes.santoro.dto.CitaDTO;

import java.time.LocalDate;
import java.util.List;

public interface CitaService {

    CitaDTO crear(CitaDTO dto);

    List<CitaDTO> listarPorFecha(LocalDate fecha);

    void cancelar(Long id);
}
