package kioskopasaportes.santoro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kioskopasaportes.santoro.model.Pasaporte;
import kioskopasaportes.santoro.model.Person;

public interface PasaporteRepository extends JpaRepository<Pasaporte, Long> {

    Optional<Pasaporte> findByNumeroPasaporte(String numeroPasaporte);
        Optional<Pasaporte> findByPersona(Person persona);

}
