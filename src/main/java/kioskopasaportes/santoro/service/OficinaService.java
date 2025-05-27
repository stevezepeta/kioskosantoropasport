package kioskopasaportes.santoro.service;

import kioskopasaportes.santoro.dto.OficinaDTO;

import java.util.List;

public interface OficinaService {

    OficinaDTO create(OficinaDTO dto);
    OficinaDTO update(Long id, OficinaDTO dto);
    OficinaDTO findById(Long id);
    List<OficinaDTO> findAll();
    void delete(Long id);
}
