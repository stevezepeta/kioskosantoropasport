package kioskopasaportes.santoro.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import kioskopasaportes.santoro.model.Pasaporte;
import kioskopasaportes.santoro.model.Person;
import kioskopasaportes.santoro.model.dto.PasaporteRequestDTO;
import kioskopasaportes.santoro.repository.PasaporteRepository;
import kioskopasaportes.santoro.repository.PersonRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasaporteService {

    private final PersonRepository personRepository;
    private final PasaporteRepository pasaporteRepository;

    public Pasaporte registrarPasaporte(PasaporteRequestDTO dto) {
        // Buscar persona por CURP
        Optional<Person> personOpt = personRepository.findByCurp(dto.getCurp());
        if (personOpt.isEmpty()) {
            throw new IllegalArgumentException("No se encontró persona con CURP " + dto.getCurp());
        }
        Person person = personOpt.get();

        // Construir y guardar pasaporte
        Pasaporte pasaporte = Pasaporte.builder()
                .persona(person)
                .numeroPasaporte(dto.getNumeroPasaporte())
                .tipoDocumento(dto.getTipoDocumento())
                .codigoPais(dto.getCodigoPais())
                .lugarEmision(dto.getLugarEmision())
                .autoridad(dto.getAutoridad())
                .fechaEmision(dto.getFechaEmision())
                .fechaExpiracion(dto.getFechaExpiracion())
                .mrz(dto.getMrz())
                .build();

        return pasaporteRepository.save(pasaporte);
    }
}
