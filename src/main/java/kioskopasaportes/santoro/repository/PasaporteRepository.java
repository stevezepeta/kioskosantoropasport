package kioskopasaportes.santoro.repository;

import kioskopasaportes.santoro.model.Pasaporte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasaporteRepository extends JpaRepository<Pasaporte, Long> {

    Optional<Pasaporte> findByNumeroPasaporte(String numeroPasaporte);
}
