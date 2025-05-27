package kioskopasaportes.santoro.service.impl;


import jakarta.transaction.Transactional;
import kioskopasaportes.santoro.dto.CitaDTO;
import kioskopasaportes.santoro.mapper.CitaMapper;
import kioskopasaportes.santoro.repository.CitaRepository;
import kioskopasaportes.santoro.service.CitaService;
import kioskopasaportes.santoro.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CitaServiceImpl implements CitaService {

    private final CitaRepository repository;
    private final CitaMapper mapper;

    @Override
    public CitaDTO crear(CitaDTO dto) {
        return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }

    @Override
    public List<CitaDTO> listarPorFecha(LocalDate fecha) {
        return mapper.toDto(repository.findByFechaCita(fecha));
    }

    @Override
    public void cancelar(Long id) {
        if (!repository.existsById(id))
            throw new ResourceNotFoundException("Cita " + id + " no encontrada");
        repository.deleteById(id);
    }
}
