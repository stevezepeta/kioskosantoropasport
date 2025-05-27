package kioskopasaportes.santoro.service.impl;

import jakarta.transaction.Transactional;
import kioskopasaportes.santoro.dto.OficinaDTO;
import kioskopasaportes.santoro.mapper.OficinaMapper;
import kioskopasaportes.santoro.repository.OficinaRepository;
import kioskopasaportes.santoro.service.OficinaService;
import kioskopasaportes.santoro.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OficinaServiceImpl implements OficinaService {

    private final OficinaRepository repo;
    private final OficinaMapper mapper;

    @Override
    public OficinaDTO create(OficinaDTO dto) {
        return mapper.toDto(repo.save(mapper.toEntity(dto)));
    }

    @Override
    public OficinaDTO update(Long id, OficinaDTO dto) {
        var ofi = repo.findById(id)
                      .orElseThrow(() -> new ResourceNotFoundException("Oficina "+id+" no encontrada"));
        mapper.updateEntity(dto, ofi);
        return mapper.toDto(repo.save(ofi));
    }

    @Override
    public OficinaDTO findById(Long id) {
        return repo.findById(id)
                   .map(mapper::toDto)
                   .orElseThrow(() -> new ResourceNotFoundException("Oficina "+id+" no encontrada"));
    }

    @Override
    public List<OficinaDTO> findAll() {
        return mapper.toDto(repo.findAll());
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id))
            throw new ResourceNotFoundException("Oficina "+id+" no encontrada");
        repo.deleteById(id);
    }
}
