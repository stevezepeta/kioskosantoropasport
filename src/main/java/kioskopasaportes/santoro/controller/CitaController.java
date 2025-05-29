package kioskopasaportes.santoro.controller;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kioskopasaportes.santoro.dto.CitaRequestDTO;
import kioskopasaportes.santoro.dto.OficinaDTO;
import kioskopasaportes.santoro.model.Cita;
import kioskopasaportes.santoro.model.Person;
import kioskopasaportes.santoro.repository.CitaRepository;
import kioskopasaportes.santoro.repository.PersonRepository;
import kioskopasaportes.santoro.service.OficinaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/cita")
@RequiredArgsConstructor
@Slf4j
public class CitaController {

    private final CitaRepository citaRepository;
    private final PersonRepository personRepository;
    private final OficinaService oficinaService;  // <-- ¡Agregado aquí!

    @PostMapping("/{personId}")
    public ResponseEntity<?> crearCita(
        @PathVariable Long personId,
        @RequestBody CitaRequestDTO request
    ) {
        Person person = personRepository.findById(personId).orElse(null);
        if (person == null) {
            return ResponseEntity.badRequest().body("Persona no encontrada");
        }

        // Buscar oficina en el catálogo JSON
        OficinaDTO oficina = oficinaService.getOficinasPorEstado(request.getEstadoId())
                                           .stream()
                                           .filter(o -> o.getId().equals(request.getOficinaId()))
                                           .findFirst()
                                           .orElse(null);

        if (oficina == null) {
            return ResponseEntity.badRequest().body("Oficina no encontrada");
        }

        Cita cita = new Cita();
        cita.setCiudadanoIdExterno(person.getIdPerson());
        cita.setPersonId(person.getIdPerson());
        cita.setOficinaId(oficina.getId());
        cita.setOficinaNombre(oficina.getNombre());
        cita.setFechaCita(LocalDate.parse(request.getFecha()));
        cita.setHoraCita(LocalTime.parse(request.getHora()));

        citaRepository.save(cita);

        return ResponseEntity.ok("Cita creada");
    }
}
